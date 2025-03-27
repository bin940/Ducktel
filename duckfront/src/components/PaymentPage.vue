<template>
  <div class="payment-container">
    <h2>결제 정보 입력</h2>
    <form @submit.prevent="submitPayment">
      <div>
        <label>이름: <input v-model="paymentData.name" required /></label>
      </div>
      <div>
        <label>전화번호: <input v-model="paymentData.phoneNumber" required /></label>
      </div>
      <div>
        <label>체크인: <input type="date" v-model="paymentData.checkInDate" readonly /></label>
      </div>
      <div>
        <label>체크아웃: <input type="date" v-model="paymentData.checkOutDate" readonly /></label>
      </div>
      <div>
        <label>결제 금액: <input v-model="paymentData.amount" readonly /></label>
      </div>
      <div>
        <label>결제 방법:
          <select v-model="paymentData.paymentMethod" required>
            <option value="credit_card">신용카드</option>
            <option value="bank_transfer">계좌이체</option>
          </select>
        </label>
      </div>
      <button type="submit">결제하기</button>
    </form>
    <router-link :to="`/places/${paymentData.accommodationId}/${paymentData.checkInDate}/${paymentData.checkOutDate}`">
      뒤로 가기
    </router-link>
  </div>
</template>

<script>
import api from "../api";

export default {
  name: "PaymentPage",
  data() {
  return {
    paymentData: {
      userId: this.$route.query.userId || "guest",
      accommodationId: this.$route.query.accommodationId,
      roomId: this.$route.query.roomId,
      name: "", 
      phoneNumber: "",
      checkInDate: this.$route.query.checkInDate,
      checkOutDate: this.$route.query.checkOutDate,
      paymentComplete: false,
      amount: Number(this.$route.query.amount),
      paymentMethod: this.$route.query.paymentMethod || "credit_card",
    },
  };
},
  methods: {
    async submitPayment() {
      try {
        const response = await api.post("/api/payment/create", this.paymentData);
        const result = response.data;
        console.log("결제 응답:", result);
        if (result.status === 200 && !result.errorCode) {
          alert("결제가 완료되었습니다!");
          this.$router.push("/");
        } else {
          alert("결제 실패: " + result.message);
        }
      } catch (error) {
        console.error("결제 오류:", error.response?.data || error.message);
        alert("결제 중 오류가 발생했습니다.");
      }
    },
  },
};
</script>

<style scoped>
.payment-container {
  max-width: 600px;
  margin: 50px auto;
  padding: 20px;
}
form {
  display: flex;
  flex-direction: column;
  gap: 15px;
}
label {
  display: block;
  margin-bottom: 5px;
}
input, select {
  padding: 5px;
  width: 100%;
}
button {
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
</style>