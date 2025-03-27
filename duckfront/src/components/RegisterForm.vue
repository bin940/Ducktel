<template>
  <div class="register-container">
    <h2>회원가입</h2>
    <form @submit.prevent="handleRegister">
      <div>
        <label for="username">아이디:</label>
        <input
          type="text"
          id="username"
          v-model="username"
          placeholder="영문자/숫자 6~16자"
        />
      </div>
      <div>
        <label for="password">비밀번호:</label>
        <input
          type="password"
          id="password"
          v-model="password"
          placeholder="8~16자"
        />
      </div>
      <div>
        <label for="phoneNumber">전화번호:</label>
        <input
          type="text"
          id="phoneNumber"
          v-model="phoneNumber"
          placeholder="10~11자 (숫자만)"
        />
      </div>
      <div>
        <label for="email">이메일:</label>
        <input
          type="email"
          id="email"
          v-model="email"
          placeholder="example@domain.com"
        />
      </div>
      <div>
        <label for="name">이름:</label>
        <input
          type="text"
          id="name"
          v-model="name"
          placeholder="2~8자"
        />
      </div>
      <button type="submit">회원가입</button>
    </form>
    <p v-if="errorMessage" class="error-message">{{ errorMessage }}</p>
    <p>
      이미 계정이 있으신가요?
      <router-link to="/">로그인</router-link>
    </p>
  </div>
</template>

<script>
import api from "../api";

export default {
  name: "RegisterForm",
  methods: {
    async handleRegister() {
      this.errorMessage = "";
      try {
        const response = await api.post("/api/users/register", {
          username: this.username,
          password: this.password,
          phoneNumber: this.phoneNumber,
          email: this.email,
          name: this.name,
        });
        const result = response.data;
        console.log("백엔드 응답:", result);

        if (result.status === 200 && !result.errorCode) {
          const userName = result.data || "사용자";
          alert(`${userName}님 회원가입을 축하드립니다!`);
          console.log("회원가입 성공:", result);
          this.$router.push("/login"); 
        } else {
          this.errorMessage = result.message || "회원가입에 실패했습니다.";
          console.error("회원가입 실패:", result);
        }
      } catch (error) {
        const errorResponse = error.response?.data;
        this.errorMessage = errorResponse?.message || "회원가입 중 오류가 발생했습니다.";
        console.error("회원가입 실패:", errorResponse || error.message);
      }
    },
  },
};
</script>

<style scoped>
.register-container {
  max-width: 400px;
  margin: 50px auto;
  padding: 20px;
  border: 1px solid #ccc;
  border-radius: 5px;
}
div {
  margin-bottom: 15px;
}
label {
  display: block;
  margin-bottom: 5px;
}
input {
  width: 100%;
  padding: 8px;
  box-sizing: border-box;
}
button {
  width: 100%;
  padding: 10px;
  background-color: #28a745;
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
}
button:hover {
  background-color: #218838;
}
.error-message {
  color: red;
  margin-top: 10px;
  font-size: 14px;
}
</style>