<template>
  <nav class="navbar">
    <div class="logo">
      <router-link to="/">DUCK TEL</router-link>
    </div>
    <div class="menu">
      <router-link to="/mypage" v-if="isLoggedIn">마이페이지</router-link>
      <button @click="handleAuth">{{ isLoggedIn ? "로그아웃" : "로그인" }}</button>
    </div>
  </nav>
</template>

<script>
import api from "../api";

export default {
  name: "NavBar",
  data() {
    return {
      isLoggedIn: false
    };
  },
  created() {
    this.checkLoginStatus();
  },
  watch: {
    '$route'() {
      this.checkLoginStatus();
    }
  },
  methods: {
    checkLoginStatus() {
      const token = localStorage.getItem("accessToken");
      console.log("accessToken:", token);
      this.isLoggedIn = !!token;
    },
    handleAuth() {
      if (this.isLoggedIn) {
        this.logout();
      } else {
        this.$router.push("/login");
      }
    },
    logout() {
      const refreshToken = localStorage.getItem("refreshToken");
      const loginType = localStorage.getItem("loginType");

      // if (!refreshToken || !loginType) {
      //   console.error("로그아웃 실패: 저장된 로그인 정보 없음");
      //   return;
      // }

      api.post("/api/auth/logout", { refreshToken, loginType })
        .then(response => {
          console.log("로그아웃 성공:", response.data);

          localStorage.removeItem("accessToken");
          localStorage.removeItem("refreshToken");
          localStorage.removeItem("loginType");

          this.isLoggedIn = false;
          this.$router.push("/");
        })
        .catch(error => {
          console.error("로그아웃 실패:", error.response?.data?.message || error.message);
        });
    }
  }
};
</script>

<style scoped>
.navbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 20px;
  background: #007bff;
  color: white;
}

.logo {
  flex: 1; 
  text-align: center;
  font-size: 1.5rem;
  font-weight: bold;
}

.logo a {
  color: white;
  text-decoration: none;
}

.menu {
  display: flex;
  gap: 15px;
  align-items: center;
}

.menu a, .menu button {
  color: white;
  text-decoration: none;
  background: none;
  border: none;
  font-size: 1rem;
  cursor: pointer;
}

.menu button {
  padding: 8px 12px;
  background-color: white;
  color: #007bff;
  border-radius: 5px;
}

.menu button:hover {
  background-color: #0056b3;
  color: white;
}
</style>