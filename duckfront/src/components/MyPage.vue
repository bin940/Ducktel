<template>
  <div class="mypage-container">
    <h2>마이페이지</h2>
    <nav>
      <router-link to="/profile">내 정보</router-link> |
      <router-link to="/booking">내 예약 조회</router-link> 
    </nav>
  </div>
</template>

<script>
export default {
  name: "MyPage",
  methods: {
    logout() {
      localStorage.removeItem("accessToken");
      localStorage.removeItem("refreshToken");
      this.$router.push("/");
    },
  },
};
</script>

<style scoped>
.mypage-container {
  max-width: 600px;
  margin: 50px auto;
  text-align: center;
}
nav a {
  margin: 0 10px;
}
</style>