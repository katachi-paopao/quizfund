import "https://cdn.jsdelivr.net/npm/vue@2.6.10/dist/vue.min.js";
import "https://cdn.jsdelivr.net/npm/vuetify@2.x/dist/vuetify.js";
import "https://cdnjs.cloudflare.com/ajax/libs/uuid/8.1.0/uuidv4.min.js";
import input_block from "./input_vue_component.js";

new Vue ({
	el: "#app",
	vuetify: new Vuetify(),
	data: {
		/* common */
		userMenu: false,

		stompClient: null,
		isPlaying: false,
		status: 'standby',
		error: null,
		qtext: null,
		input_length: null,
		input_arbitrary: false,
		input_type: null,
		enter_function: null,
		result: null,
		resultList: null,
		leftTime: 20,
		timerId: null,
		count: 0
	},
	components: {
		"key-input": input_block
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

		start: function(id) {
			this.isPlaying = true;
			this.status = "start";
			this.stompClient = Stomp.over(new WebSocket("ws://" + location.host + "/websocket/endpoint"));
			this.stompClient.debug = null;
			let self = this;
			this.stompClient.connect({}, function(frame){
				self.connectionId = uuidv4();
				self.stompClient.subscribe("/topic/game/" + self.connectionId, self.onPush.bind(this));
				self.stompClient.send("/app/websocket/game/" + self.connectionId, {}, id);
			}, function(frame){
				self.status = "error";
				self.error = "通信に失敗しました";
			})
		},
		onPush: function(message) {
			let msg = JSON.parse(message.body)
			if (msg.action == "start") {
				this.status = "start";
			} else if (msg.action == "question") {
				this.count++;
				this.status = "playing";
				let msg = JSON.parse(message.body)
				let q = msg.object;
				this.qtext = q.text;
				this.$refs.input.clear();
				this.input_arbitrary = (q.ans_length == null);
				this.input_length = q.ans_length ?? 12;
				this.input_type = q.ans_type;
				this.enter_function = this.sendAnswer;
				this.countdown();
			} else if (msg.action == "check") {
				if (msg.object) {
					this.result = "正解";
				} else {
					this.result = "不正解";
				}
				this.status = "result";
			} else if  (msg.action == "timeout") {
				this.enter_function = null;
				this.countstop();
				this.result = "時間切れ";
				this.status = "result";
			} else if (msg.action == "end") {
				this.status = "end";
				this.isPlaying = false;

				this.qtext = null;
				this.$refs.input.clear();
				this.input_length = null;
				this.count = 0;

				let msg = JSON.parse(message.body)
				this.resultList = msg.object;
				this.disconnect();
			} else if (msg.action == "error") {
				this.status = "error";
				this.error = msg.object;

				this.qtext = null;
				this.$refs.input.clear();
				this.input_length = null;
				this.count = 0;

				this.disconnect();
			}
		},
		sendAnswer: function() {
			this.enter_function = null;
			this.countstop();
			let msg = {
					action: "answer",
					object: this.$refs.input.text
			}
			this.stompClient.send("/app/websocket/game/answer", {}, JSON.stringify(msg));
		},
		disconnect: function() {
			if (this.stompClient) {
				this.stompClient.disconnect();
				this.stompClient = null;
			}
		},
		countdown: function() {
			this.leftTime = 20;
			let that = this;
			this.timerId = setInterval(function(){
				if (that.leftTime > 0) that.leftTime--;
			}, 1000);
			setTimeout(function(){
				clearInterval(this.timerId)
			}, 21 * 1000);
		},
		countstop: function() {
			if (this.timerId != null) {
				clearInterval(this.timerId);
				this.timerId = null;
			}
		},
		interpret_input_type: function(typeNum) {
				switch(typeNum) {
				case 0:
					return "ひらがな";
					break;
				case 1:
					return "カタカナ";
					break;
				case 2:
					return "アルファベット";
					break;
				case 3:
					return "数字";
					break;
				default:
					return "未定義";
			}
		}
	}
})