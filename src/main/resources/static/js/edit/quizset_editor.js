import "https://cdn.jsdelivr.net/npm/vue@2.6.10/dist/vue.js";
import "https://cdn.jsdelivr.net/npm/vuetify@2.x/dist/vuetify.js";
import "https://cdn.jsdelivr.net/npm/vuelidate@0.7.7/dist/vuelidate.min.js";
import "https://cdn.jsdelivr.net/npm/vuelidate@0.7.7/dist/validators.min.js";

Vue.use(window.vuelidate.default)
const {required, maxLength, between, helpers} = window.validators;
new Vue ({
	el: "#app",
	vuetify: new Vuetify(),
	data: {
		/* common */
		userMenu: false,

		configDialog: false,
		moveDialog: false,

		config: {
			isEditting: false,
			name: "",
			intro: "",
			isRandom: false,
			gameNum: 1
		},
		leftList: {
			headers: [
				{text: ""},
				{text: "#"},
				{text: "問題文"},
				{text: "解答"}
			],
			items: [],
			totalPages: 1,
			page: 1,
			keyword: ""
		},
		rightList: {
			headers: [
				{text: ""},
				{text: "#"},
				{text: "出題順"},
				{text: "問題文"},
				{text: "解答"},
				{text: "移動"}
			],
			items: [],
			totalPages: 1,
			page: 1,
			idList: [],
			keyword: "",
			totalElements: 0,
			targetId: null,
			pos:	null
		},

		csrfToken: document.querySelector('meta[name="_csrf"]').content
	},
	computed: {
		customRightItems: function() {
			return this.rightList.items.map(item => {
				let answer = [];
				item.answers.forEach(
					elem => {
						answer.push(elem.answer);
					}
				)
				return {
					"id": item.id,
					"serial": item.serial + 1,
					"text": item.text,
					"answers": answer.join("／"),
					"isChecked": item.isChecked
				};
			});
		},
		customLeftItems: function() {
			return this.leftList.items.map(item => {
				let answer = [];
				item.answers.forEach(
					elem => {
						answer.push(elem.answer);
					}
				);
				return {
					"id": item.id,
					"text": item.text,
					"answers": answer.join("／"),
					"isChecked": item.isChecked,
					"isIncluded": item.isIncluded
				};
			});
		},
		nameErrors() {
			const errors = [];
			if (!this.$v.config.name.$dirty) return errors;
			!this.$v.config.name.required && errors.push('問題セットのタイトルを入力してください');
			!this.$v.config.name.maxLength && errors.push("タイトルは100文字以内で入力してください");
	        return errors;
		},
		introErrors() {
			const errors = [];
			if (!this.$v.config.intro.$dirty) return errors;
			!this.$v.config.intro.maxLength && errors.push("説明文は200文字以内で入力してください");
	        return errors;
		},
		gameNumErrors() {
			const errors = [];
			if (!this.$v.config.gameNum.$dirty) return errors;
			!this.$v.config.gameNum.between && errors.push("出題数は最少で1問、最大で50問です");
	        return errors;
		}
	},
	methods: {
		/* common */
		toHome: function() {
			location.href="/home";
		},
		toEdit:	function() {
			location.href="/edit"
		},
		toPlay:	function() {
			location.href="/game"
		},

		toEditQuizset: function() {
			location.href="/edit/quizset"
		},

		updateRightList(responseData) {
			let self = this;

			this.rightList.totalPages = responseData.totalPages;
			this.rightList.items = responseData.content;
			this.rightList.items.forEach(item => {
				self.$set(item, 'isChecked', false);
			})
		},
		allOn: function(items) {
			let self = this;

			if (items == this.leftList.items) {
				items.forEach(
					elem => {
						if (!elem.isIncluded) {
							self.$set(elem, 'isChecked', true);
						}
					}
				);
			} else {
				items.forEach(elem => {self.$set(elem, 'isChecked', true)});
			}
		},
		allOff: function(items) {
			let self = this;
			if (items == this.leftList.items) {
				items.forEach(
					elem => {
						if (!elem.isIncluded) {
							self.$set(elem, 'isChecked', false);
						}
					}
				);
			} else {
				items.forEach(elem => {self.$set(elem, 'isChecked', false)});
			}
		},
		changePage: function(e, list) {
			let self = this;

			if (list == this.leftList) {
				axios.get("/edit/fetch?p=" + e + "&q=" + list.keyword)
				.then(response => {
					list.page = e;
					list.totalPages = response.data.totalPages;
					list.items = response.data.content;
					list.items.forEach(item => {
						self.$set(item, 'isChecked', false);
						self.$set(item, 'isIncluded', self.rightList.idList.includes(item.id));
					})
				});
			} else { // rightList
				axios.post(window.location + "/fetch?p=" + e + "&q=" + list.keyword,
						{
							"exIds": list.excludedIdList,
							"idList": list.idList
						},
						{
							headers: {
								"X-CSRF-TOKEN": this.csrfToken
							}
						}
				)
				.then(response => {
					this.updateRightList(response.data);
					let totalElements = response.data.totalElements;

					list.page = e;
				});
			}
		},
		leftKeywordSearch: function() {
			let keyword = this.$refs.leftKeywordInput.lazyValue;
			let self = this;
			axios.get("/edit/fetch?p=1&q=" + keyword)
			.then(response => {
				self.leftList.page = 1;
				self.leftList.totalPages = response.data.totalPages;
				self.leftList.items = response.data.content;

				self.leftList.items.forEach(item => {
					self.$set(item, 'isChecked', false);
					self.$set(item, 'isIncluded', self.rightList.idList.includes(item.id));
				});

				this.leftList.keyword = keyword;
			});
		},
		rightKeywordSearch: function() {
			let keyword = this.$refs.rightKeywordInput.lazyValue;
			let self = this;
			let list = this.rightList;
			axios.post(window.location + "/fetch?p=1&q=" + keyword,
					{
						"exIds": list.excludedIdList,
						"idList": list.idList
					},

					{
						headers: {
							"X-CSRF-TOKEN": this.csrfToken
						}
					}
			).then(response => {
				list.totalPages = response.data.totalPages;
				list.page = 1;
				list.items = response.data.content;

				let totalElements = response.data.totalElements;

				list.items.forEach(item => {
					self.$set(item, 'isChecked', false);
				});
				list.keyword = keyword;
			});
		},
		add: function() {
			let r = this.rightList;

			this.leftList.items.forEach( (item, index) => {
				if (item.isChecked) {
					item.isIncluded = true;
					item.isChecked = false;
					r.idList.push(item.id);
				}
			})

			this.changePage(this.rightList.page, this.rightList);
		},
		remove: function() {
			let delCount = 0;
			let r = this.rightList;

			this.customRightItems.forEach( (item, index) => {
				if (item.isChecked) {
					let idListIndex = r.idList.indexOf(item.id);

					if (idListIndex != -1) {
						r.idList.splice(idListIndex, 1);
					}

					this.rightList.items.splice(index - delCount, 1);
					delCount++;
					this.rightList.totalElements--;
				}
			})

			this.leftList.items.forEach(item => {
				if (!this.rightList.idList.includes(item.id)) {
					item.isChecked = false;
					item.isIncluded = false;
				}
			})

			this.changePage(this.rightList.page, this.rightList);
		},
		move:function() {
			let r = this.rightList;

			let idListIndex = r.idList.indexOf(r.targetId);

			if (idListIndex != -1) {
				let id = r.idList.splice(idListIndex, 1)[0];
				r.idList.splice(r.pos >= 1 ? r.pos - 1 : 0, 0, id);
			}

			this.changePage(this.rightList.page, this.rightList);

			this.rightList.pos = null;
			this.moveDialog = false;
		},
		save: function() {
			this.$refs.list.value = this.rightList.idList;
			document.getElementById("editForm").submit();
		},
		updateConfig: function() {
			if (this.$v.$invalid) return false;
			document.getElementById("configForm").submit();
		},
	},
	mounted: async function() {
		let table = document.querySelectorAll(".v-data-table tbody")[1];

		let r = this.rightList;

		await axios.get(window.location +"/idList")
		.then(response => {
			this.config.name = response.data.config[0];
			this.config.intro = response.data.config[1];
			this.config.isRandom = response.data.config[2];
			this.config.gameNum = response.data.config[3];
			r.idList = response.data.idList;

			r.isLoading = false;
		});

		await axios.post(window.location + "/fetch?p=1", null, {
			headers: {
				'Accept': 'application/json',
				'Content-Type': 'application/json',
				"X-CSRF-TOKEN": this.csrfToken
			}
		})
		.then(response => {
			r.totalElements = response.data.totalElements;
			this.updateRightList(response.data);
		});

		let l = this.leftList;
		axios.get("/edit/fetch?p=1")
		.then(response => {
			l.totalPages = response.data.totalPages;
			l.items = response.data.content;
			l.items.forEach(item => {
				this.$set(item, 'isChecked', false);
				this.$set(item, 'isIncluded', r.idList.includes(item.id));
			})
		});
	},

	validations: function(){
		return {
			config: {
				name: { required, maxLength: maxLength(100) },
				intro: { maxLength: maxLength(200) },
				gameNum: { between: between(1, 50) }
			}
		}
	}
});