<template>
  <div class="login-container">
    <h2>로그인</h2>
    <form @submit.prevent="handleLogin">
      <div>
        <label>아이디: <input v-model="username" required /></label>
      </div>
      <div>
        <label>비밀번호: <input type="password" v-model="password" required /></label>
      </div>
      <button type="submit">로그인</button>
    </form>
    <router-link to="/register">회원가입</router-link>

    <div class="social-login">
      <button @click="socialLogin('google')" class="google-btn">구글로 로그인</button>
      <button @click="socialLogin('kakao')" class="kakao-btn">카카오로 로그인</button>
      <button @click="socialLogin('naver')" class="naver-btn">네이버로 로그인</button>
    </div>
  </div>
</template>

<script>
import api from "../api"; 

export default {
  name: "LoginForm",
  data() {
    return {
      username: "",
      password: "",
    };
  },
  mounted() {
    const urlParams = new URLSearchParams(window.location.search);
    if (urlParams.has("accessToken")) {
      this.handleSocialLoginCallback();
    }
  },
  methods: {
  async handleLogin() {
    try {
      const formData = new URLSearchParams();
      formData.append("username", this.username);
      formData.append("password", this.password);

      const response = await api.post("/api/auth/login", formData, {
        headers: {
          "Content-Type": "application/x-www-form-urlencoded",
        },
      });

      console.log("로그인 성공:", response.data);
      this.processLoginResponse(response.data);

    } catch (error) {
      const errorResponse = error.response?.data;
      console.error("로그인 실패:", errorResponse?.message || error.message);
    }
  },

  processLoginResponse(data) { 
    const tokens = data.data;
    if (!tokens || !tokens.accessToken || !tokens.refreshToken) {
    console.error("로그인 응답에 토큰 없음", tokens);
    return;
  }

  console.log("토큰 저장 중...");
  localStorage.setItem("accessToken", tokens.accessToken);
  localStorage.setItem("refreshToken", tokens.refreshToken);
  localStorage.setItem("loginType", tokens.loginType);

  const redirectUrl = sessionStorage.getItem("redirectUrl") || "/";
  sessionStorage.removeItem("redirectUrl");

  console.log(`로그인 성공, ${redirectUrl}로 이동`);
  this.$router.push(redirectUrl);
  },

  socialLogin(provider) {
    const redirectUri = `${window.location.origin}/login?redirect=${encodeURIComponent(this.$route.fullPath)}`;
    const authUrl = `http://localhost:8090/oauth2/authorization/${provider}?redirect_uri=${encodeURIComponent(redirectUri)}`;

    console.log(`소셜 로그인 리다이렉트: ${authUrl}`);
    window.location.href = authUrl;
  },

  handleSocialLoginCallback() {
    const urlParams = new URLSearchParams(window.location.search);
    const accessToken = urlParams.get("accessToken");
    const refreshToken = urlParams.get("refreshToken");
    const loginType = urlParams.get("loginType");

    if (accessToken && refreshToken && loginType) {
      console.log("소셜 로그인 성공 - 토큰:", accessToken);
      localStorage.setItem("accessToken", accessToken);
      localStorage.setItem("refreshToken", refreshToken);
      localStorage.setItem("loginType", loginType);
      console.log("loginType:", loginType);

      console.log("저장된 accessToken 확인:", localStorage.getItem("accessToken"));
      console.log("저장된 refreshToken 확인:", localStorage.getItem("refreshToken"));
      console.log("저장된 loginType 확인:", localStorage.getItem("loginType"));
      const redirectUrl = sessionStorage.getItem("redirectUrl") || "/";
      sessionStorage.removeItem("redirectUrl");
      this.$router.push(redirectUrl);

      window.history.replaceState({}, document.title, "/login");
    } else {
      console.error("소셜 로그인 실패: 필요한 토큰 정보가 없습니다.");
    }
  }
}
};
</script>

<style scoped>
.login-container {
  max-width: 400px;
  margin: 50px auto;
  padding: 20px;
}
form {
  display: flex;
  flex-direction: column;
  gap: 15px;
}
button {
  padding: 10px;
  background-color: #007bff;
  color: white;
  border: none;
  border-radius: 5px;
}
button:hover {
  background-color: #0056b3;
}
.social-login {
  margin-top: 20px;
  display: flex;
  flex-direction: column;
  gap: 10px;
}
.google-btn {
  background-color: #4285f4;
}
.google-btn:hover {
  background-color: #357abd;
}
.kakao-btn {
  background-color: #fee500;
  color: #3c1e1e;
}
.kakao-btn:hover {
  background-color: #e6ce00;
}
.naver-btn {
  background-color: #03c75a;
}
.naver-btn:hover {
  background-color: #02a44f;
}
</style>