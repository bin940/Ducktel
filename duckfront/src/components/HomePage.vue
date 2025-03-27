<template>
  <div class="home-container">
    <header>
      <h2>홈페이지</h2>
    </header>
    <div>
      <label for="category">카테고리 선택:</label>
      <select id="category" v-model="selectedCategory" @change="goToSubHome">
        <option value="" disabled>카테고리를 선택하세요</option>
        <option value="호텔">호텔</option>
        <option value="리조트">리조트</option>
        <option value="펜션">펜션</option>
        <option value="게스트하우스">게스트하우스</option>
        <option value="모텔">모텔</option>
      </select>
    </div>

    <!-- 할인 숙소 -->
    <div v-if="homeData && homeData.discountAccommodations">
      <h3>할인 숙소</h3>
      <div class="accommodations-container">
        <div
          v-for="item in homeData.discountAccommodations"
          :key="item.accommodationId"
          class="accommodation-card"
          @click="goToPlaces(item.accommodationId)"
        >
          <img
            v-if="item.image && item.image.length"
            :src="item.image[0]"
            alt="숙소 이미지"
            class="accommodation-image"
          />
          <div class="accommodation-details">
            <strong>{{ item.name }}</strong>
            <p>{{ item.location }}</p>
            <p v-if="item.discount">할인: {{ item.discount }}%</p>
            <p>좋아요: {{ item.likeCount }}</p>
          </div>
        </div>
      </div>
    </div>

    <!-- 인기 숙소 -->
    <div v-if="homeData && homeData.favoriteAccommodations">
      <h3>인기 숙소</h3>
      <div class="accommodations-container">
        <div
          v-for="item in homeData.favoriteAccommodations"
          :key="item.accommodationId"
          class="accommodation-card"
          @click="goToPlaces(item.accommodationId)"
        >
          <img
            v-if="item.image && item.image.length"
            :src="item.image[0]"
            alt="숙소 이미지"
            class="accommodation-image"
          />
          <div class="accommodation-details">
            <strong>{{ item.name }}</strong>
            <p>{{ item.location }}</p>
            <p v-if="item.discount">할인: {{ item.discount }}%</p>
            <p>좋아요: {{ item.likeCount }}</p>
          </div>
        </div>
      </div>
    </div>

    <!-- 계절 추천 숙소 -->
    <div v-if="homeData && homeData.seasonalAccommodations">
      <h3>계절 추천 숙소</h3>
      <div class="accommodations-container">
        <div
          v-for="item in homeData.seasonalAccommodations"
          :key="item.accommodationId"
          class="accommodation-card"
          @click="goToPlaces(item.accommodationId)"
        >
          <img
            v-if="item.image && item.image.length"
            :src="item.image[0]"
            alt="숙소 이미지"
            class="accommodation-image"
          />
          <div class="accommodation-details">
            <strong>{{ item.name }}</strong>
            <p>{{ item.location }}</p>
            <p v-if="item.discount">할인: {{ item.discount }}%</p>
            <p>좋아요: {{ item.likeCount }}</p>
          </div>
        </div>
      </div>
    </div>

    <p v-if="!homeData">데이터를 불러오는 중...</p>
  </div>
</template>

<script>
import api from "../api";

export default {
  name: "HomePage",
  data() {
    return {
      homeData: null,
      selectedCategory: "",
      checkInDate: new Date().toISOString().split("T")[0],
      checkOutDate: new Date(Date.now() + 86400000).toISOString().split("T")[0],
    };
  },
  computed: {
    isLoggedIn() {
      return !!localStorage.getItem("accessToken");
    },
  },
  created() {
    this.fetchHomeData();
  },
  methods: {
    async fetchHomeData() {
      try {
        const response = await api.get("/api/home");
        const result = response.data;
        if (result.status === 200 && !result.errorCode) {
          this.homeData = result.data;
          console.log("홈 데이터 조회 성공:", this.homeData);
        } else {
          console.error("홈 데이터 조회 실패:", result);
        }
      } catch (error) {
        console.error("홈 데이터 조회 오류:", error.response?.data || error.message);
      }
    },
    logout() {
      localStorage.removeItem("accessToken");
      localStorage.removeItem("refreshToken");
      this.$router.push("/");
    },
    goToSubHome() {
      if (this.selectedCategory) {
        this.$router.push(`/sub-home/${this.selectedCategory}`);
      }
    },
    goToPlaces(accommodationId) {
      console.log("Navigating to:", `/places/${accommodationId}/${this.checkInDate}/${this.checkOutDate}`);
      this.$router.push(`/places/${accommodationId}/${this.checkInDate}/${this.checkOutDate}`);
    },
  },
};
</script>

<style scoped>
.home-container {
  max-width: 1200px;
  margin: 50px auto;
  padding: 20px;
}
.accommodations-container {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
}
.accommodation-card {
  width: 220px;
  border: 1px solid #ddd;
  border-radius: 5px;
  padding: 10px;
  cursor: pointer;
  transition: transform 0.2s;
}
.accommodation-card:hover {
  transform: scale(1.05);
}
.accommodation-image {
  width: 100%;
  height: 150px;
  object-fit: cover;
  border-radius: 5px;
}
.accommodation-details {
  text-align: center;
}
select {
  padding: 5px;
  margin-left: 10px;
}
</style>