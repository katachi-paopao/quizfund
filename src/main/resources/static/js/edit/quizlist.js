import editor from "./edit_vue_component.js";

new Vue ({
	el: "#app",
	vuetify: new Vuetify(),
	components: {
		"editor": editor
	},
	data: {
		/* common */
		userMenu: false,

		headers: [
			{text: "#"},
			{text: "問題文"},
			{text: "解答"},
			{text: "正答率"},
			{text: ""},
			{text: ""}
		],
		items: [],
		totalPages: 1,
		page: 1,
		keyword: "",

		createDialog: false,
		editDialog: false,
		confirmDeleteDialog: false,

		editTarget: null,
		csrfToken: document.querySelector('meta[name="_csrf"]').content
	},
	mounted: function() {
		let self = this;
		axios.get("edit/fetch?p=1")
		.then(response => {
			self.totalPages = response.data.totalPages;
			self.items = response.data.content;
		});
	},
	computed: {
		customItems: function() {
			return this.items.map(item => {
				let answer = [];
				item.answers.forEach(
						elem => {
							answer.push(elem.answer);
						}
				);
				return {
					"id": item.id,
					"text": item.text,
					"isArbitrary": item.option.ans_length == null,
					"ans_type": item.option.ans_type,
					"answers": answer.join("／"),
					"correct_rate" : item.appearance <= 0 ? 0 : Math.round(item.correct / item.appearance * 100)
				};
			});
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

		changePage: function(e) {
			let self = this;
			axios.get("edit/fetch?p=" + e)
			.then(response => {
				self.page = e;
				self.totalPages = response.data.totalPages;
				self.items = response.data.content;
			})
			.catch(error => {
				self.items = null;
			});
		},
		keywordSearch: function() {
			let keyword = this.$refs.keywordInput.lazyValue ?? '';
			let self = this;
			axios.get("edit/fetch?p=1&q=" + keyword)
			.then(response => {
				self.page = 1;
				self.totalPages = response.data.totalPages;
				self.items = response.data.content;

				this.keyword = keyword;
			});
		},
		updateItem: function(formId) {
			this.$refs.editor.$v.text.$touch();
			this.$refs.editor.$v.answer.$touch();
			this.$refs.editor.$v.alt_answers.$touch();

			if (this.$refs.editor.$v.$invalid) return false;
			document.getElementById(formId).submit();
		},
		deleteItem: function(targetId) {
			let self = this;
			axios.post("edit/delete/" + targetId, null, {
				headers: {
					"X-CSRF-TOKEN": this.csrfToken
				}
			})
			.then(response => {
				self.page = 1;
				self.totalPages = response.data.totalPages;
				self.items = response.data.content;
			});
		}
	}
});