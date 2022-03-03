import "https://cdn.jsdelivr.net/npm/vue@2.6.10/dist/vue.min.js";
import "https://cdn.jsdelivr.net/npm/vuetify@2.x/dist/vuetify.js";


new Vue ({
	el: "#app",
	vuetify: new Vuetify(),
	data: {
		/* common */
		userMenu: false,

		index: 0,
		images: [
			{src: "/assets/game.png"},
			{src: "/assets/making.png"},
			{src: "/assets/quizset.png"}
		],
	},
	methods: {
		toHome: function() {
			location.href="/home";
		},
		toEdit:	function() {
			location.href="/edit"
		},
		toPlay:	function() {
			location.href="/game"
		},
		toSignup: function() {
			location.href="/signup"
		}
	}
});