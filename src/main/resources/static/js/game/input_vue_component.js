const input_block = {
	template:
	`<div id="input">
		<span v-for="n of block_num" class="characterBlock">{{(n-1 == focus) ? (roman_buffer == "" ? text.charAt(n-1) : roman_buffer) : text.charAt(n-1)}}</span>
	</div>`,
	props: ["block_num", "type_mode", "enter_function"],
	data: function() {
		return {
			text: "",
			focus: 0,
			roman_buffer: "",
			roman_table_hiragana: {
				A: "あ", I: "い", U: "う", E: "え", O: "お",
				KA: "か", KI: "き", KU: "く", KE: "け", KO: "こ",
				SA: "さ", SI: "し", SHI: "し", SU: "す", SE: "せ", SO: "そ",
				TA: "た", TI: "ち", CHI: "ち", TU: "つ", TSU: "つ", TE: "て", TO: "と",
				NA: "な", NI: "に", NU: "ぬ", NE: "ね", NO: "の",
				HA: "は", HI: "ひ", HU: "ふ", FU: "ふ", HE: "へ", HO: "ほ",
				MA: "ま", MI: "み", MU: "む", ME: "め", MO: "も",
				YA: "や", YI: "い", YU: "ゆ", YE:"いぇ", YO: "よ",
				RA: "ら", RI: "り", RU: "る", RE: "れ", RO: "ろ",
				WA: "わ", WO: "を", NN: "ん", WU: "う",
				GA: "が", GI: "ぎ", GU: "ぐ", GE: "げ", GO: "ご",
				ZA: "ざ", ZI: "じ", JI: "じ", ZU: "ず", ZE: "ぜ", ZO: "ぞ",
				DA: "だ", DI: "ぢ", DU: "づ", DE: "で", DO: "ど",
				BA: "ば", BI: "び", BU: "ぶ", BE: "べ", BO: "ぼ",
				PA: "ぱ", PI: "ぴ", PU: "ぷ", PE: "ぺ", PO: "ぽ",
				XA: "ぁ", XI: "ぃ", XU: "ぅ", XE: "ぇ", XO: "ぉ",
				LA: "ぁ", LI: "ぃ", LU: "ぅ", LE: "ぇ", LO: "ぉ",
				XYA: "ゃ", XYI: "ぃ", XYU: "ゅ", XYE:"ぇ",  XYO: "ょ",
				LYA: "ゃ", LYI: "ぃ", LYU: "ゅ", LYE: "ぇ", LYO: "ょ",
				KYA: "きゃ", KYI: "きぃ", KYU: "きゅ", KYE: "きぇ", KYO: "きょ",
				GYA: "ぎゃ", GYU: "ぎゅ", GYO: "ぎょ",
				SHA: "しゃ", SHU: "しゅ", SHE: "しぇ", SHO: "しょ",
				SYA: "しゃ", SYI: "しぃ", SYU: "しゅ", SYE: "しぇ", SYO: "しょ",
				JA: "じゃ", JYI: "じぃ", JU: "じゅ", JE: "じぇ", JYE: "じぇ", JO: "じょ",
				ZYA: "じゃ", ZYI: "じぃ", ZYU: "じゅ", ZYE: "じぇ", ZYO: "じょ",
				CHA: "ちゃ", CHU: "ちゅ", CHE: "ちぇ", CHO: "ちょ",
				CYA: "ちゃ", CYI:"ちぃ", CYU: "ちゅ", CYE: "ちぇ", CYO: "ちょ",
				TYA: "ちゃ", TYI: "ちぃ", TYU: "ちゅ", TYE: "ちぇ", TYO: "ちょ",
				THA: "てゃ", THI: "てぃ", THU: "てゅ", THE: "てぇ", THO: "てょ",
				DHA: "でゃ", DHI: "でぃ", DHU: "でゅ", DHE: "でぇ", DHO: "でょ",
				DWA: "どぁ", DWI: "どぃ", DWU: "どぅ", DWE: "どぇ", DWO: "どぉ",
				NYA: "にゃ", NYI: "にぃ", NYU: "にゅ", NYE:"にぇ", NYO: "にょ",
				HYA: "ひゃ", HYI: "ひぃ", HYU: "ひゅ", HYE: "ひぇ", HYO: "ひょ",
				FA: "ふぁ", FI: "ふぃ", FE: "ふぇ", FO:"ふぉ",
				BYA: "びゃ", BYI: "びぃ", BYU: "びゅ", BYE: "びぇ", BYO: "びょ",
				PYA: "ぴゃ", PYI: "ぴぃ", PYU: "ぴゅ", PYE: "ぴぇ", PYO: "ぴょ",
				MYA: "みゃ", MYI: "みぃ", MYU: "みゅ", MYE: "みぇ", MYO: "みょ",
				RYA: "りゃ", RYI: "りぃ", RYU: "りゅ", RYE: "りぇ", RYO: "りょ"
			},
			roman_table_katakana: {
				A: "ア", I: "イ", U: "ウ", E: "エ", O: "オ",
				KA: "カ", KI: "キ", KU: "ク", KE: "ケ", KO: "コ",
				SA: "サ", SI: "シ", SHI: "シ", SU: "ス", SE: "セ", SO: "ソ",
				TA: "タ", TI: "チ", CHI: "チ", TU: "ツ", TSU: "ツ", TE: "テ", TO: "ト",
				NA: "ナ", NI: "ニ", NU: "ヌ", NE: "ネ", NO: "ノ",
				HA: "ハ", HI: "ヒ", HU: "フ", FU: "フ", HE: "ヘ", HO: "ホ",
				MA: "マ", MI: "ミ", MU: "ム", ME: "メ", MO: "モ",
				YA: "ヤ", YI: "イ", YU: "ユ", YE:"イェ", YO: "ヨ",
				RA: "ラ", RI: "リ", RU: "ル", RE: "レ", RO: "ロ",
				WA: "ワ", WO: "ヲ", NN: "ン", WU: "ウ",
				GA: "ガ", GI: "ギ", GU: "グ", GE: "ゲ", GO: "ゴ",
				ZA: "ザ", ZI: "ジ", JI: "ジ", ZU: "ズ", ZE: "ゼ", ZO: "ゾ",
				DA: "ダ", DI: "ヂ", DU: "ヅ", DE: "デ", DO: "ド",
				BA: "バ", BI: "ビ", BU: "ブ", BE: "ベ", BO: "ボ",
				PA: "パ", PI: "ピ", PU: "プ", PE: "ペ", PO: "ポ",
				XA: "ァ", XI: "ィ", XU: "ゥ", XE: "ェ", XO: "ォ",
				LA: "ァ", LI: "ィ", LU: "ゥ", LE: "ェ", LO: "ォ",
				XYA: "ャ", XYI: "ィ", XYU: "ュ", XYE:"ェ",  XYO: "ョ",
				LYA: "ャ", LYI: "ィ", LYU: "ュ", LYE: "ェ", LYO: "ョ",
				KYA: "キャ", KYI: "キィ", KYU: "キュ", KYE: "キェ", KYO: "キョ",
				GYA: "ギャ", GYU: "ギュ", GYO: "ギョ",
				SHA: "シャ", SHU: "シュ", SHE: "シェ", SHO: "ショ",
				SYA: "シャ", SYI: "シィ", SYU: "シュ", SYE: "シェ", SYO: "ショ",
				JA: "ジャ", JYI: "ジィ", JU: "ジュ", JE: "ジェ", JYE: "ジェ", JO: "ジョ",
				ZYA: "ジャ", ZYI: "ジィ", ZYU: "ジュ", ZYE: "ジェ", ZYO: "ジョ",
				CHA: "チャ", CHU: "チュ", CHE: "チェ", CHO: "チョ",
				CYA: "チャ", CYI:"チィ", CYU: "チュ", CYE: "チェ", CYO: "チョ",
				TYA: "チャ", TYI: "チィ", TYU: "チュ", TYE: "チェ", TYO: "チョ",
				THA: "テャ", THI: "ティ", THU: "テュ", THE: "テェ", THO: "テョ",
				DHA: "デャ", DHI: "ディ", DHU: "デュ", DHE: "デェ", DHO: "デョ",
				DWA: "ドァ", DWI: "ドィ", DWU: "ドゥ", DWE: "ドェ", DWO: "ドォ",
				NYA: "ニャ", NYI: "ニィ", NYU: "ニュ", NYE:"ニェ", NYO: "ニョ",
				HYA: "ヒャ", HYI: "ヒィ", HYU: "ヒュ", HYE: "ヒェ", HYO: "ヒョ",
				FA: "ファ", FI: "フィ", FE: "フェ", FO:"フォ",
				BYA: "ビャ", BYI: "ビィ", BYU: "ビュ", BYE: "ビェ", BYO: "ビョ",
				PYA: "ピャ", PYI: "ピィ", PYU: "ピュ", PYE: "ピェ", PYO: "ピョ",
				MYA: "ミャ", MYI: "ミィ", MYU: "ミュ", MYE: "ミェ", MYO: "ミョ",
				RYA: "リャ", RYI: "リィ", RYU: "リュ", RYE: "リェ", RYO: "リョ"
			}
		}
	},
	methods: {
		onKeyDown: function(e) {
			if (this.enter_function == null) return;

			if (e.keyCode == 8) { // バックスペースの入力
				if (this.roman_buffer.length == 0) {
					if (this.focus != this.text.length-1) this.focus = Math.max(0, this.focus-1);
					this.text = this.text.slice(0, -1);
				} else {
					this.roman_buffer = "";
				}
			}

			if (this.type_mode == 3 && e.keyCode >= 48 && e.keyCode <= 57) { // 数字の入力
				if (this.text.length < this.block_num) {
					this.text += String.fromCharCode(e.keyCode+65248);
					this.focus = Math.min(this.focus+1, this.block_num-1);
				} else {
					this.text = this.text.slice(0,-1) + String.fromCharCode(e.keyCode+65248);
				}
			}

			if (this.type_mode == 3 && e.keyCode >= 96 && e.keyCode <= 105) { // 数字の入力（テンキー)
				if (this.text.length < this.block_num) {
					this.text += String.fromCharCode(e.keyCode+65200);
					this.focus = Math.min(this.focus+1, this.block_num-1);
				} else {
					this.text = this.text.slice(0,-1) + String.fromCharCode(e.keyCode+65200);
				}
			}

			if (this.type_mode == 2 && e.keyCode >= 65 && e.keyCode <= 90) { // 英字の入力
				if (this.text.length < this.block_num) {
					this.text += String.fromCharCode(e.keyCode+65248);
					this.focus = Math.min(this.focus+1, this.block_num-1);
				} else {
					this.text = this.text.slice(0,-1) + String.fromCharCode(e.keyCode+65248);
				}
			}

			if (this.type_mode == 0 && e.keyCode >= 65 && e.keyCode <= 90) { // ひらがなの入力
				this.roman_buffer += String.fromCharCode(e.keyCode);
				let hiragana = this.roman_table_hiragana[this.roman_buffer];
				if (hiragana != undefined) {
					for (let i=0; i<hiragana.length; i++) {
						if (this.focus < this.block_num-1) {
							this.text += hiragana.charAt(i);
							this.focus = Math.min(this.focus+1, this.block_num-1);
						} else {
							if (this.text.length < this.block_num) {
								this.text += hiragana.charAt(i);
							} else {
								this.text = this.text.slice(0,-1) + hiragana.charAt(i);
							}
							break;
						}
					}

					this.roman_buffer = "";
				} else if (this.roman_buffer.length <= 2) {
					if (this.roman_buffer.length == 2 && this.roman_buffer.charAt(0) == "N" && this.roman_buffer.charAt(1) != "Y") {
						if (this.text.length < this.block_num) {
							this.text += "ん"
						} else {
							this.text = this.text.slice(0,-1) + hiragana.charAt(i);
						}
						this.focus = Math.min(this.focus+1, this.block_num-1);
						this.roman_buffer = this.roman_buffer.charAt(1);
					}
					if (this.roman_buffer.length == 2 && this.roman_buffer.charAt(0) == this.roman_buffer.charAt(1)) {
						if (this.text.length < this.block_num) {
							this.text += "っ"
						} else {
							this.text = this.text.slice(0,-1) + hiragana.charAt(i);
						}
						this.focus = Math.min(this.focus+1, this.block_num-1);
						this.roman_buffer = this.roman_buffer.charAt(1);
					}
				} else { // roman_buffer.length > 2
					this.roman_buffer = this.roman_buffer.slice(0, -1);
				}

				if (this.text.length == this.block_num && this.roman_buffer.length > 0) {
					this.text = this.text.slice(0,-1);
				}
			}

			if (this.type_mode == 1 && e.keyCode >= 65 && e.keyCode <= 90) { // カタカナの入力
				this.roman_buffer += String.fromCharCode(e.keyCode);
				let katakana = this.roman_table_katakana[this.roman_buffer];
				if (katakana != undefined) {
					for (let i=0; i<katakana.length; i++) {
						if (this.focus < this.block_num-1) {
							this.text += katakana.charAt(i);
							this.focus = Math.min(this.focus+1, this.block_num-1);
						} else {
							if (this.text.length < this.block_num) {
								this.text += katakana.charAt(i);
							} else {
								this.text = this.text.slice(0,-1) + katakana.charAt(i);
							}
							break;
						}
					}

					this.roman_buffer = "";
				} else if (this.roman_buffer.length <= 2) {
					if (this.roman_buffer.length == 2 && this.roman_buffer.charAt(0) == "N" && this.roman_buffer.charAt(1) != "Y") {
						if (this.text.length < this.block_num) {
							this.text += "ン"
						} else {
							this.text = this.text.slice(0,-1) + katakana.charAt(i);
						}
						this.focus = Math.min(this.focus+1, this.block_num-1);
						this.roman_buffer = this.roman_buffer.charAt(1);
					}
					if (this.roman_buffer.length == 2 && this.roman_buffer.charAt(0) == this.roman_buffer.charAt(1)) {
						if (this.text.length < this.block_num) {
							this.text += "ッ"
						} else {
							this.text = this.text.slice(0,-1) + katakana.charAt(i);
						}
						this.focus = Math.min(this.focus+1, this.block_num-1);
						this.roman_buffer = this.roman_buffer.charAt(1);
					}
				} else { // roman_buffer.length > 2
					this.roman_buffer = this.roman_buffer.slice(0, -1);
				}

				if (this.text.length == this.block_num && this.roman_buffer.length > 0) {
					this.text = this.text.slice(0,-1);
				}
			}

			if (e.keyCode == 189) { // 長音符（ー）の入力
				if (this.text.length < this.block_num) {
					this.text += "ー"
					this.focus = Math.min(this.focus+1, this.block_num-1);
				} else {
					this.text = this.text.slice(0,-1) + "ー";
				}
				this.roman_buffer = "";
			}

			if (e.keyCode == 13) { // エンターキーの入力
				if (this.enter_function != null) {
					this.enter_function();
					this.roman_buffer = "";
				}
			}
		},
		clear: function() {
			this.text = "";
			this.focus = 0;
		}
	},
	mounted: function(){
		document.addEventListener('keydown', this.onKeyDown);
	},
	beforeDestroy: function() {
		document.removeEventListener('keydown', this.onKeyDown);
	}
}

export default input_block;