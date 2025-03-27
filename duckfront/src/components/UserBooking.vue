<template>
  <div class="booking-container">
    <h2>내 예약 조회</h2>
    <div v-if="reservations && reservations.length">
      <ul>
        <li v-for="reservation in reservations" :key="reservation.bookingId">
          <p><strong>숙소:</strong> {{ reservation.accommodation.name }}</p>
          <p><strong>룸:</strong> {{ reservation.room.name }}</p>
          <p><strong>체크인:</strong> {{ reservation.checkIn }}</p>
          <p><strong>체크아웃:</strong> {{ reservation.checkOut }}</p>
          <p><strong>결제 상태:</strong> {{ reservation.paymentCompleted ? "완료" : "미완료" }}</p>
          <button @click="editReservation(reservation)">수정</button>
          <button @click="deleteReservation(reservation.bookingId)" class="delete-btn">삭제</button>
        </li>
      </ul>

      <!-- 예약 수정 모달 -->
      <div v-if="showEditModal" class="modal">
        <div class="modal-content">
          <h3>예약 수정</h3>
          <form @submit.prevent="updateBooking">
            <div>
              <label>체크인:</label>
              <input v-model="editBooking.checkIn" type="date" />
            </div>
            <div>
              <label>체크아웃:</label>
              <input v-model="editBooking.checkOut" type="date" />
            </div>
            <button type="submit">저장</button>
            <button @click="showEditModal = false">취소</button>
          </form>
        </div>
      </div>
    </div>
    <p v-else-if="reservations">예약 내역이 없습니다.</p>
    <p v-else>예약 정보를 불러오는 중...</p>
    <router-link to="/mypage">마이페이지로 돌아가기</router-link>
  </div>
</template>

<script>
import api from "../api";

export default {
  name: "UserBooking",
  data() {
    return {
      reservations: null,
      showEditModal: false,
      editBooking: {},
    };
  },
  created() {
    this.fetchReservations();
  },
  methods: {
    async fetchReservations() {
      try {
        const response = await api.get("/api/users/book");
        const result = response.data;
        if (result.status === 200 && !result.errorCode) {
          this.reservations = result.data;
          console.log("예약 조회 성공:", this.reservations);
        } else {
          console.error("예약 조회 실패:", result);
        }
      } catch (error) {
        console.error("예약 조회 오류:", error.response?.data || error.message);
      }
    },
    editReservation(reservation) {
      this.editBooking = { ...reservation };
      this.showEditModal = true;
    },
    async updateBooking() {
      try {
        const response = await api.put("/api/users/book", {
          bookingId: this.editBooking.bookingId,
          checkIn: this.editBooking.checkIn,
          checkOut: this.editBooking.checkOut,
          numberOfPerson: this.editBooking.numberOfPerson,
          paymentCompleted: this.editBooking.paymentCompleted,
          accommodation: this.editBooking.accommodation,
          room: this.editBooking.room,
        });
        const result = response.data;
        if (result.status === 200 && !result.errorCode) {
          this.showEditModal = false;
          this.fetchReservations();
          alert("예약 수정 성공!");
        } else {
          alert("예약 수정 실패: " + (result.message || "오류 발생"));
        }
      } catch (error) {
        alert("예약 수정 오류: " + (error.response?.data.message || error.message));
      }
    },
    async deleteReservation(bookingId) {
      if (confirm("이 예약을 삭제하시겠습니까?")) {
        try {
          const response = await api.delete(`/api/users/book/${bookingId}`);
          const result = response.data;
          if (result.status === 200 && !result.errorCode) {
            this.fetchReservations();
            alert("예약 삭제 성공!");
          } else {
            alert("예약 삭제 실패: " + (result.message || "오류 발생"));
          }
        } catch (error) {
          alert("예약 삭제 오류: " + (error.response?.data.message || error.message));
        }
      }
    },
  },
};
</script>

<style scoped>
.booking-container {
  max-width: 600px;
  margin: 50px auto;
  padding: 20px;
}
ul {
  list-style: none;
  padding: 0;
}
li {
  padding: 10px 0;
  border-bottom: 1px solid #eee;
}
button {
  margin-left: 10px;
  padding: 5px 10px;
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
}
.delete-btn:hover {
  background-color: #c82333;
}
.modal {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
}
.modal-content {
  background: white;
  padding: 20px;
  border-radius: 5px;
}
.modal div {
  margin-bottom: 15px;
}
</style>