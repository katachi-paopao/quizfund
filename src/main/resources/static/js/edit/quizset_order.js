import "https://cdn.jsdelivr.net/npm/vue@2.6.10/dist/vue.js";
import "https://cdn.jsdelivr.net/npm/vuetify@2.x/dist/vuetify.js";
import "https://cdnjs.cloudflare.com/ajax/libs/Sortable/1.14.0/Sortable.min.js";

Vue.directive('scroll', {
	inserted: function (el, binding) {
		let f = function (evt) {
			if (binding.value(evt, el)) {
				window.removeEventListener('scroll', f)
			}
		}
		window.addEventListener('scroll', f)
		}
	}
)

new Vue ({
	el: "#app",
	vuetify: new Vuetify(),
	data: {
		/* common */
		userMenu: false,

		moveDialog: false,

		items: [],
		sortedItems: [],
		idList: [],
		targetId: null,
		pos:	null,
		totalPage: null,
		upBound: 1,
		downBound: 1,

		csrfToken: document.querySelector('meta[name="_csrf"]').content
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

		insert: function() {
			// let list = document.querySelectorAll("li");
			// let item = list[this.pos-1].remove();

			/* idListへの順序変更反映 */
			let removeIndex = this.idList.indexOf(this.targetId);
			if (removeIndex != -1) {
				this.idList.splice(removeIndex, 1);
				this.idList.splice(this.pos-1, 0, this.targetId);
			}

			/* 画面上への順序変更反映 */
			for (let i = 0; i < this.items.length; i++) {
				if (this.items[i].id == this.targetId) {
					let item = this.items.splice(i, 1)[0];
					this.items.splice(this.pos-1, 0, item)
					break;
				}
			}

			this.moveDialog = false;
		},
		handleScroll: function (ev, el) {
			let a = document.documentElement.scrollTop + window.innerHeight;
			let b = document.getElementById("app").clientHeight;

			if ( Math.abs(a-b) <= 1 ) {
				if (this.downBound < this.totalPage) {
					axios.post("/edit/quizset/1/fetch?p=" + (this.downBound + 1), null, {
						headers: {
							'Accept': 'application/json',
							'Content-Type': 'application/json',
							"X-CSRF-TOKEN": this.csrfToken
						}
					})
					.then(response => {
						let i = 1;
						response.data.content.forEach( item => {
							this.$set(item, 'isVisible', true);
							item.serial = 20 * (this.downBound) + i;

							let answers = [];
							item.answers.forEach( answer => {
								answers.push(answer.answer);
							});
							item.textAnswer = answers.join("／");

							this.items.push(item);
							i++;
						});
						this.downBound++;
					});
				}
			}
		},
		save: function() {
			this.$refs.list.value = this.idList;
			document.getElementById("editForm").submit();
		},
		loadThird: function() {
			axios.post("/edit/quizset/1/fetch?p=3", null, {
				headers: {
					'Accept': 'application/json',
					'Content-Type': 'application/json',
					"X-CSRF-TOKEN": this.csrfToken
				}
			})
			.then(response => {
				let i = 1;
				response.data.content.forEach( item => {
					this.$set(item, 'isVisible', true);
					item.serial = 20 * 2 + i;

					let answers = [];
					item.answers.forEach( answer => {
						answers.push(answer.answer);
					});
					item.textAnswer = answers.join("／");

					this.items.push(item);
					i++;
				});
				this.downBound = 3;
			});
		}
	},
	mounted: async function() {
		await axios.post("/edit/quizset/1/fetch?p=1", null, {
			headers: {
				'Accept': 'application/json',
				'Content-Type': 'application/json',
				"X-CSRF-TOKEN": this.csrfToken
			}
		})
		.then(response => {
			let cnt = 1;
			this.items = response.data.content;
			this.items.forEach( item => {
				this.$set(item, 'isVisible', true);
				this.$set(item, 'serial', cnt++);

				let answers = [];
				item.answers.forEach( answer => {
					answers.push(answer.answer);
				});
				item.textAnswer = answers.join("／");
			})
		});

		this.sortedItems = this.items;

		await axios.get("/edit/quizset/1/idList")
		.then(response => {
			this.idList = response.data.idList;
			this.totalPage = Math.ceil(this.idList.length / 20 );
		});

		let list = document.getElementById("list")
		let sortable = Sortable.create(list, {
			onEnd: ({newIndex, oldIndex}) => {
				let item = this.idList.splice(oldIndex, 1)[0];
				this.idList.splice(newIndex, 0, item);

				/*
				let itemsIndex = -1;
				for (let i = 0; i < this.items.length; i++) {
					if (this.items[i].id == item) {
						itemsIndex = i;
					}
				}

				if (itemsIndex != -1) {
					this.items[itemsIndex].serial = newIndex + 1;
				}
				*/

				/*
				if (newIndex > oldIndex) {
					for (let i=oldIndex+1; i <= newIndex; i++) {
						this.items[i].serial--;
					}
				} else {
					for (let i=oldIndex; i > newIndex; i--) {
						this.items[i].serial++;
					}
				}
				*/
			}
		})
	}
})