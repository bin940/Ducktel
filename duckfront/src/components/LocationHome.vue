<template>
  <div class="location-home-container">
    <div v-if="locationHomeData && locationHomeData.locationAccommodations">
      <h3>{{ location }} 숙소 목록</h3>
      <ul>
        <li
          v-for="item in locationHomeData.locationAccommodations"
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
    </div>
    <p v-else>데이터를 불러오는 중...</p>
  </div>
</template>

<script>
import api from "../api";

export default {
  name: "LocationHome",
  data() {
    return {
      locationHomeData: null,
      checkInDate: new Date().toISOString().split("T")[0], // 오늘
      checkOutDate: new Date(Date.now() + 86400000).toISOString().split("T")[0], // 내일
    };
  },
  computed: {
    category() {
      return this.$route.params.category;
    },
    location() {
      return this.$route.params.location;
    },
  },
  created() {
    this.fetchLocationHomeData();
  },
  methods: {
    async fetchLocationHomeData() {
      try {
        const response = await api.get(`/api/sub-home/${this.category}/${this.location}`);
        const result = response.data;
        if (result.status === 200 && !result.errorCode) {
          this.locationHomeData = result.data;
          console.log("Location Home 데이터 조회 성공:", this.locationHomeData);
        } else {
          console.error("Location Home 데이터 조회 실패:", result);
        }
      } catch (error) {
        console.error("Location Home 데이터 조회 오류:", error.response?.data || error.message);
      }
    },
    goToPlaces(accommodationId) {
      this.$router.push(`/places/${accommodationId}/${this.checkInDate}/${this.checkOutDate}`);
    },
  },
};
</script>

<style scoped>
.location-home-container {
  max-width: 800px;
  margin: 50px auto;
  padding: 20px;
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