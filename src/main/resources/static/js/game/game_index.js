import "https://cdn.jsdelivr.net/npm/vue@2.6.10/dist/vue.min.js";
import "https://cdn.jsdelivr.net/npm/vuetify@2.x/dist/vuetify.js";

new Vue ({
	el: "#app",
	vuetify: new Vuetify(),
	data: {
		/* common */
		userMenu: false,

		keyword: "",
		items: []
	},
	methods: {
		/* common */
		toHome: function() {
			location.href="/home";
		},
		toEdit:	function() {
			location.href="/edit";
		},
		toPlay:	function() {
			location.href="/game";
		},

		toGamePage: function(id) {
			location.href="/game/play/" + id;
		},
		keywordSearch: function() {
			let keyword = this.$refs.keywordInput.lazyValue ?? '';
			let self = this;
			axios.get("/game/index/fetch?p=1&q=" + keyword)
			.then(response => {
				self.items = response.data.content;

				this.keyword = keyword;
			});
		},
	},
	mounted: function() {
		let self = this;
		axios.get("/game/index/fetch?p=1")
		.then(response => {
			self.items = response.data.content;
		});
	}
});