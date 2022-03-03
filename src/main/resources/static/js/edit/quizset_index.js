import "https://cdn.jsdelivr.net/npm/vue@2.6.10/dist/vue.js";
import "https://cdn.jsdelivr.net/npm/vuetify@2.x/dist/vuetify.js";

new Vue ({
	el: "#app",
	vuetify: new Vuetify(),
	data: {
		/* common */
		userMenu: false,

		createDialog: false,
		confirmDeleteDialog: false,

		items: [],
		editTarget: null,
		keyword: "",
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

		toQuizsetEditor: function(id) {
			location.href="/edit/quizset/" + id
		},
		toEditQuizlist: function() {
			location.href="/edit"
		},
		openQuizset: function(item) {
			if (item.gameNum <= 0) {
				window.alert("出題数0の問題セットは公開できません");
			}

			let self = this;

			axios.post("/edit/quizset/open/" +item.id, null, {
				headers: {
					"X-CSRF-TOKEN": this.csrfToken
				}
			}).then(response => {
				if (response.data) item.open = true;
			});
		},
		closeQuizset: function(item) {
			let self = this;

			axios.post("/edit/quizset/close/" + item.id, null, {
				headers: {
					"X-CSRF-TOKEN": this.csrfToken
				}
			}).then(response => {
				if (response.data) item.open = false;
			});
		},
		deleteQuizset: function(targetId) {
			let self = this;
			axios.post("/edit/quizset/delete/" + targetId, null, {
				headers: {
					"X-CSRF-TOKEN": this.csrfToken
				}
			})
			.then(response => {
				self.items = response.data.content;
			});
		},
		keywordSearch: function() {
			let keyword = this.$refs.keywordInput.lazyValue ?? '';
			let self = this;
			axios.get("/edit/quizset/fetch?p=1&q=" + keyword)
			.then(response => {
				self.items = response.data.content;

				this.keyword = keyword;
			});
		},
	},
	mounted: function() {
		let self = this;
		axios.get("/edit/quizset/fetch?p=1")
		.then(response => {
			self.items = response.data.content;
		});
	},
});