import "https://cdn.jsdelivr.net/npm/vue@2.6.10/dist/vue.js";
import "https://cdn.jsdelivr.net/npm/vuetify@2.x/dist/vuetify.js";
import "https://cdn.jsdelivr.net/npm/vuelidate@0.7.7/dist/vuelidate.min.js";
import "https://cdn.jsdelivr.net/npm/vuelidate@0.7.7/dist/validators.min.js";

Vue.use(window.vuelidate.default)
const {required, maxLength, helpers} = window.validators;
const editor = {
	template:
	`
	<v-container>
			<v-container>
				<v-row justify="end">
					<p v-text="'正解率 : ' + correct_rate + '%'" v-if="correct_rate"></p>
				</v-row>
				<v-row justify="center">
					<div class="wrapper__textarea">
					<v-textarea label="問題文" id="text" name="text" v-model="text" :counter="200"
					:error-messages="isEditting ? null : textErrors"
					style="font-size: 24px; width: 23em"
					@focus="isEditting = true"
					@focusout="isEditting = false"
					@input="$v.text.$touch()"
					@blur="$v.text.$touch()"
					clearable outlined required>
					</v-textarea>
					</div>
				</v-row>
			</v-container>
			<v-row>
				<v-col cols="12" md="6">
					<v-text-field id="answer" name="answer" v-model="answer" label="解答" :counter="12"
					:error-messages="isEditting ? null : answerErrors"
					@focus="isEditting = true"
					@focusout="isEditting = false"
					@input="$v.answer.$touch() ; answer_type = checkAnsType(answer)"
					@blur="$v.answer.$touch()"
					clearable outlined>
					</v-text-field>
				</v-col>
				<v-col cols="12" md="6">
					<v-text-field name="alt_answers" v-model="alt_answers[0]" label="別解1" :counter="12"
					:error-messages="isEditting ? null : alt_answersErrors(0)"
					@focus="isEditting = true"
					@focusout="isEditting = false"
					@input="$v.alt_answers.$touch()"
					@blur="$v.alt_answers.$touch()"
					clearable outlined>
					</v-text-field>
				</v-col>
			</v-row>
			<v-row>
				<v-col cols="12" md="6">
					<v-text-field name="alt_answers" v-model="alt_answers[1]" label="別解2" :counter="12"
					:error-messages="isEditting ? null : alt_answersErrors(1)"
					@focus="isEditting = true"
					@focusout="isEditting = false"
					@input="$v.alt_answers.$touch()"
					@blur="$v.alt_answers.$touch()"
					clearable outlined>
					</v-text-field>
				</v-col>
				<v-col cols="12" md="6">
					<v-text-field name="alt_answers" v-model="alt_answers[2]" label="別解3" :counter="12"
					:error-messages="isEditting ? null : alt_answersErrors(2)"
					@focus="isEditting = true"
					@focusout="isEditting = false"
					@input="$v.alt_answers.$touch()"
					@blur="$v.alt_answers.$touch()"
					clearable outlined>
					</v-text-field>
				</v-col>
			</v-row>
			<v-row>
				<v-container>
					<v-checkbox label="任意長文字列" v-model="isArbitrary"></v-checkbox>
					<input type="hidden" id="arbitrary" name="arbitrary" :value="isArbitrary">
				</v-container>
			</v-row>
	</v-container>
	`,
	props: ["item"],
	vuetify: new Vuetify(),
	data: function() {
		if (this.item != null) { // 更新
			let answers = this.item.answers.split("／");

			return {
				isEditting: false,
				patterns: ["^[\\u3040-\\u309F|ー]+$", "^[\\u30A0-\\u30FF]+$", "^[Ａ-Ｚ]+$", "^[０-９]+$"],
				text: this.item.text,
				answer: answers[0],
				alt_answers: [answers[1], answers[2], answers[3]],
				isArbitrary: this.item.isArbitrary,
				answer_type: this.item.ans_type,
				correct_rate: this.item.correct_rate
			}
		} else { // 新規作成
			return {
				isEditting: false,
				patterns: ["^[\\u3040-\\u309F|ー]+$", "^[\\u30A0-\\u30FF]+$", "^[Ａ-Ｚ]+$", "^[０-９]+$"],
				text: null,
				answer: null,
				alt_answers: [null, null, null],
				isArbitrary: false,
				answer_type: -1,
				correct_rate: null
			}
		}
	},
	methods: {
		checkAnsType: function(text) {
			let type = -1;
			for (let i in this.patterns) {
				if ( new RegExp(this.patterns[i]).test(text) ) {
					type = Number(i);
					break;
				}
			}
			return type;
		}
	},
	watch: {
		item: function() {
			let answers = this.item.answers.split("／");
			this.text = this.item.text,
			this.answer = answers[0],
			this.alt_answers = [answers[1], answers[2], answers[3]],
			this.isArbitrary = this.item.isArbitrary,
			this.answer_type = this.item.ans_type
		}
	},
	computed: {
		textErrors() {
			const errors = [];
			if (!this.$v.text.$dirty) return errors;
			!this.$v.text.required && errors.push('問題文を入力してください');
			!this.$v.text.maxLength && errors.push('問題文は200文字以内で入力してください');
	        return errors;
		},
		answerErrors() {
			const errors = [];
			if (!this.$v.answer.$dirty) return errors;
			!this.$v.answer.required && errors.push('解答を入力してください');
			!this.$v.answer.maxLength && errors.push('解答は12文字以内で入力してください');
			!this.$v.answer_type.pattern && errors.push('文字種はひらがな、カタカナ、全角英大文字、全角数字のいずれか1種類のみ使用できます');

	        return errors;
		},
		alt_answersErrors() {
			return function(i) {
				const errors = [];
				!this.$v.alt_answers.$each[i].maxLength && errors.push('解答は12文字以内で入力してください');
				!this.$v.alt_answers.$each[i].length && errors.push('解答と文字長が異なります');
				!this.$v.alt_answers.$each[i].pattern && errors.push('解答と文字種が異なります');

				return errors;
			}
		}
	},
	validations: function(){
		return {
			text: { required, maxLength: maxLength(200) },
			answer: { required, maxLength: maxLength(12) },
			alt_answers: {
				$each: {
					maxLength: maxLength(12),
					length: (val) => {
							if (!val || this.isArbitrary || helpers.len(val) == 0) return true;
							return helpers.len(val) == (this.answer ? this.answer.length : 0);
					},
					pattern: (val) => {
						if (!val || helpers.len(val) == 0 ) return true;
						return this.checkAnsType(val) == this.answer_type;
					}
				}
			},
			answer_type: {
				pattern: (val) => (val != -1)
			}
		}
	}
}

export default editor;