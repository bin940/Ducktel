<template>
  <div class="places-container">
    <h2>숙소 상세 정보</h2>
    <div>
      <label>체크인: <input type="date" v-model="checkInDate" @change="updatePlaces" /></label>
      <label>체크아웃: <input type="date" v-model="checkOutDate" @change="updatePlaces" /></label>
    </div>
    <div v-if="placesData">
      <div class="accommodation-info">
        <h3>{{ placesData.accommodation.name }}</h3>
        <p><strong>위치:</strong> {{ placesData.accommodation.location }}</p>
        <p><strong>태그:</strong> {{ placesData.accommodation.tag || "없음" }}</p>
        <p><strong>설명:</strong> {{ placesData.accommodation.explanation || "없음" }}</p>
        <p><strong>서비스:</strong> {{ placesData.accommodation.serviceInfo || "없음" }}</p>
        <p v-if="placesData.accommodation.image && placesData.accommodation.image.length">
          <img :src="placesData.accommodation.image[0]" alt="숙소 이미지" style="max-width: 300px;" />
        </p>
        <p v-if="placesData.accommodation.discount"><strong>할인:</strong> {{ placesData.accommodation.discount }}%</p>
        <p v-if="placesData.accommodation.season"><strong>계절:</strong> {{ placesData.accommodation.season }}</p>
        <p><strong>좋아요:</strong> {{ placesData.accommodation.likeCount }}</p>
          <button @click="toggleLike">❤️ 좋아요</button>
      </div>
      <div class="rooms-list" v-if="placesData.rooms && placesData.rooms.length">
        <h3>예약 가능한 방</h3>
        <ul>
          <li v-for="room in placesData.rooms" :key="room.roomId">
            <div class="room-info">
              <strong>{{ room.name }}</strong><br />
              <span>최대 인원: {{ room.maxCapacity }}</span><br />
              <span>최소 인원: {{ room.minCapacity }}</span><br />
              <span>가격: {{ room.price }}원</span><br />
              <span>설명: {{ room.explanation || "없음" }}</span><br />
              <span>서비스: {{ room.serviceInfo || "없음" }}</span><br />
              <span>태그: {{ room.tag || "없음" }}</span><br />
              <span v-if="room.images && room.images.length">
               <img :src="room.images[0]" alt="방 이미지" style="max-width: 200px;" />
               <img :src="room.images[1]" alt="방 이미지" style="max-width: 200px;" />
              </span>
            </div>
            <button
              class="reserve-btn"
              :disabled="room.availableRooms === 0"
              @click="handleReservation(room.roomId, room.price)"
            >
              예약하기
            </button>
          </li>
        </ul>
      </div>
      <p v-else>예약 가능한 방이 없습니다.</p>
    </div>
    <p v-else>데이터를 불러오는 중...</p>
    <router-link :to="`/sub-home/${placesData?.accommodation?.category || '호텔'}`">카테고리로 돌아가기</router-link> |
    <router-link to="/">홈으로 돌아가기</router-link>
  </div>
</template>

<script>
import api from "../api";

export default {
  name: "PlaceDetail",
  data() {
    return {
      placesData: null,
      checkInDate: this.$route.params.checkInDate,
      checkOutDate: this.$route.params.checkOutDate,
    };
  },
  computed: {
    accommodationId() {
      return this.$route.params.accommodationId;
    },
    isLoggedIn() {
      return !!localStorage.getItem("accessToken");
    },
  },
  created() {
    this.fetchPlacesData();
  },
  methods: {
    async fetchPlacesData() {
      try {
        const response = await api.get(
          `/api/places/${this.accommodationId}/${this.checkInDate}/${this.checkOutDate}`
        );
        const result = response.data;
        if (result.status === 200 && !result.errorCode) {
          this.placesData = result.data;
          console.log("Places 데이터 조회 성공:", this.placesData);
        } else {
          console.error("Places 데이터 조회 실패:", result);
        }
      } catch (error) {
        console.error("Places 데이터 조회 오류:", error.response?.data || error.message);
      }
    },
    updatePlaces() {
      if (this.checkInDate && this.checkOutDate) {
        this.$router.push(`/places/${this.accommodationId}/${this.checkInDate}/${this.checkOutDate}`);
        this.fetchPlacesData();
      }
    },
    handleReservation(roomId, price) {
  const userId = localStorage.getItem("userId");
  if (!userId) {
    console.error("userId가 저장되지 않음, 로그인 상태 확인 필요");
  }
  
  
  const paymentData = {
    userId: userId || "guest", // undefined 대신 null 체크
    accommodationId: this.accommodationId,
    roomId: roomId,
    name: this.placesData.accommodation.name,
    phoneNumber: "010-1234-5678", // 임시값
    checkInDate: this.checkInDate,
    checkOutDate: this.checkOutDate,
    paymentComplete: false,
    amount: price,
    paymentMethod: "credit_card",
  };

  if (!this.isLoggedIn) {
    console.log("로그인 필요, 로그인 페이지로 이동");
    this.$router.push({
      path: "/login",
      query: { redirect: this.$route.fullPath },
    });
  } else {
    console.log("로그인 완료, 결제 페이지로 이동:", paymentData);
    this.$router.push({
      path: "/payment",
      query: paymentData,
    });
      }
    },
   async toggleLike() {
      const token = localStorage.getItem("accessToken");
      if (!token) {
        alert("로그인이 필요합니다.");
        this.$router.push({ path: "/login", query: { redirect: this.$route.fullPath } });
        return;
      }

       try {
        const response = await api.post("/api/users/likes", null, {
          headers: {
            Authorization: `Bearer ${token}`,
          },
          params: {
            accommodationId: this.accommodationId,
          },
        });

        console.log("좋아요 처리 결과:", response.data);
        this.fetchPlacesData();
      } catch (error) {
        console.error("좋아요 처리 오류:", error.response?.data || error.message);
      }
    }
  }
};
</script>

<style scoped>
.places-container {
  max-width: 800px;
  margin: 50px auto;
  padding: 20px;
}
.accommodation-info {
  margin-bottom: 30px;
}
.rooms-list ul {
  list-style: none;
  padding: 0;
}
.rooms-list li {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 5px;
}
.room-info {
  flex-grow: 1;
}
.reserve-btn {
  padding: 8px 16px;
  background-color: #007bff;
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
}
.reserve-btn:disabled {
  background-color: #ccc;
  cursor: not-allowed;
}
</style>