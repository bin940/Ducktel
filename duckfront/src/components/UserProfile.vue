<template>
  <div class="profile-container">
    <h2>내 정보</h2>
    <div v-if="userInfo !== null">
      
      <form @submit.prevent="updateProfile">
        <div>
          <p>아이디: {{ userInfo.username }}</p>
        </div>
        <div>
          <label>이메일:</label>
          <input v-model="editUser.email" placeholder="이메일" />
        </div>
        <div>
          <label>전화번호:</label>
          <input v-model="editUser.phoneNumber" placeholder="전화번호" />
        </div>
        <button type="submit">수정</button>
      </form>

      <h3 v-if="isLocalLogin">비밀번호 재설정</h3>
      <form v-if="isLocalLogin" @submit.prevent="resetPassword">
        <div>
          <label>새 비밀번호:</label>
          <input type="password" v-model="newPassword" placeholder="새 비밀번호" />
        </div>
        <button type="submit">재설정</button>
      </form>

      <button @click="deleteProfile" class="delete-btn">회원 탈퇴</button>
    </div>
    <p v-else>정보를 불러오는 중...</p>
    <router-link to="/mypage">마이페이지로 돌아가기</router-link>
  </div>
</template>

<script>
import api from "../api";

export default {
  name: "UserProfile",
  data() {
    return {
      userInfo: null,
      editUser: { name: "", email: "", phoneNumber: "" },
      newPassword: "",
    };
  },
  created() {
    this.fetchProfile();
  },
  computed: {
    isLocalLogin() {
      return localStorage.getItem("loginType") === "LOCAL";
    }
  },
  methods: {
    async fetchProfile() {
      try {
        const response = await api.get("/api/users/profile");
        const result = response.data;
        console.log("백엔드 응답 전체:", result);
        if (result.status === 200 && !result.errorCode) {
          this.userInfo = result.data;
          this.editUser = { ...result.data };
          console.log("프로필 조회 성공:", this.userInfo);
        } else {
          console.error("프로필 조회 실패:", result);
        }
      } catch (error) {
        console.error("프로필 조회 오류:", error.response?.data || error.message);
      }
    },
    async updateProfile() {
      try {
        const response = await api.put("/api/users/profile", this.editUser);
        const result = response.data;
        if (result.status === 200 && !result.errorCode) {
          this.userInfo = result.data;
          alert("정보 수정 성공!");
        } else {
          alert("정보 수정 실패: " + (result.message || "오류 발생"));
        }
      } catch (error) {
        alert("정보 수정 오류: " + (error.response?.data.message || error.message));
      }
    },
    async resetPassword() {
      try {
        const response = await api.post("/api/users/password-reset", { password: this.newPassword });
        const result = response.data;
        console.log("비밀번호 재설정 응답:", result);
        if (result === "비밀번호가 변경 되었습니다.") {
          alert("비밀번호 재설정 성공!");
          this.newPassword = "";
        } else {
          alert("비밀번호 재설정 실패");
        }
      } catch (error) {
        alert("비밀번호 재설정 오류: " + (error.response?.data || error.message));
      }
    },
    async deleteProfile() {
      if (confirm("정말 탈퇴하시겠습니까?")) {
        try {
          const response = await api.delete("/api/users/profile/" + this.userInfo.userId);
          const result = response.data;
          if (result === "삭제되었습니다") {
            localStorage.removeItem("accessToken");
            localStorage.removeItem("refreshToken");
            alert("회원 탈퇴 성공!");
            this.$router.push("/");
            window.location.reload();
          } else {
            alert("회원 탈퇴 실패");
          }
        } catch (error) {
          alert("회원 탈퇴 오류: " + (error.response?.data || error.message));
        }
      }
    },
  },
};
</script>

<style scoped>
.profile-container {
  max-width: 600px;
  margin: 50px auto;
  padding: 20px;
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
  padding: 10px 20px;
  background-color: #007bff;
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
}
button:hover {
  background-color: #0056b3;
}
.delete-btn {
  background-color: #dc3545;
  margin-top: 20px;
}
.delete-btn:hover {
  background-color: #c82333;
}
</style>