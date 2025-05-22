<template>
  <div class="sub-home-container">
    <h2>{{ category }} 페이지</h2>
    <div>
      <label for="location">지역 선택:</label>
      <select id="location" v-model="selectedLocation" @change="goToLocationHome">
        <option value="" disabled>지역을 선택하세요</option>
        <option value="서울 강남구">서울 강남구</option>
        <option value="부산 해운대">부산 해운대</option>
        <option value="제주 서귀포">제주 서귀포</option>
        <option value="강원도 평창">강원도 평창</option>
        <option value="서울 종로구">서울 종로구</option>
      </select>
    </div>
    <div v-if="subHomeData">
      <h3>{{ category }} 숙소 목록</h3>
      <ul v-if="subHomeData.categoryAccommodations && subHomeData.categoryAccommodations.length">
        <li
          v-for="item in subHomeData.categoryAccommodations"
          :key="item.accommodationId"
          @click="goToPlaces(item.accommodationId)"
          style="cursor: pointer;"
        >
          <strong>{{ item.name }}</strong> - {{ item.location }}<br />
          <img
            v-if="item.image && item.image.length"
            :src="item.image[0]"
            alt="숙소 이미지"
            class="accommodation-image"
          /><br />
          <span>태그: {{ item.tag || "없음" }}</span><br />
          <span>설명: {{ item.explanation || "없음" }}</span><br />
          <span>서비스: {{ item.serviceInfo || "없음" }}</span><br />
          <span v-if="item.discount">할인: {{ item.discount }}%</span><br />
          <span v-if="item.season">계절: {{ item.season }}</span><br />
          <span>좋아요: {{ item.likeCount }}</span>
        </li>
      </ul>
      <p v-else>{{ category }} 카테고리에 숙소가 없습니다.</p>
    </div>
    <p v-else>데이터를 불러오는 중...</p>
    
  </div>
</template>

<script>
import api from "../api";

export default {
  name: "SubHome",
  data() {
    return {
      subHomeData: null,
      selectedLocation: "",
      checkInDate: new Date().toISOString().split("T")[0], // 오늘
      checkOutDate: new Date(Date.now() + 86400000).toISOString().split("T")[0], // 내일
    };
  },
  computed: {
    category() {
      return this.$route.params.category;
    },
  },
  created() {
    this.fetchSubHomeData();
  },
  watch: {
          '$route.params.category': 'fetchSubHomeData'
  },
  methods: {
    async fetchSubHomeData() {
      this.selectedLocation = "";
      try {
        const response = await api.get(`/api/home/${encodeURIComponent(this.category)}`);
        const result = response.data;
        if (result.status === 200 && !result.errorCode) {
          this.subHomeData = result.data;
          console.log("Home 데이터 조회 성공:", this.subHomeData);
        } else {
          console.error("Home 데이터 조회 실패:", result);
        }
      } catch (error) {
        console.error("Home 데이터 조회 오류:", error.response?.data || error.message);
      }
    },
    goToLocationHome() {
      if (!this.selectedLocation) return;

      this.$router.push({
        name: "LocationHome",
        params: {
          category: this.category,
          location: this.selectedLocation
        }
      });
    },
    goToPlaces(accommodationId) {
      this.$router.push(`/places/${accommodationId}/${this.checkInDate}/${this.checkOutDate}`);
    },
  },
};
</script>

<style scoped>
.sub-home-container {
  max-width: 800px;
  margin: 50px auto;
  padding: 20px;
}
select {
  padding: 5px;
  margin-left: 10px;
}
ul {
  list-style: none;
  padding: 0;
}
li {
  margin-bottom: 20px;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 5px;
}
li:hover {
  background-color: #f5f5f5;
}
.accommodation-image {
  width: 50%;
  height: 150px;
  object-fit: cover;
  border-radius: 5px;
}
</style>