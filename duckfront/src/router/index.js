import { createRouter, createWebHistory } from "vue-router";
import HomePage from "../components/HomePage.vue";
import LoginForm from "../components/LoginForm.vue";
import RegisterForm from "../components/RegisterForm.vue";
import MyPage from "../components/MyPage.vue";
import UserProfile from "../components/UserProfile.vue";
import UserBooking from "../components/UserBooking.vue";
import SubHome from "../components/SubHome.vue";
import LocationHome from "../components/LocationHome.vue";
import Places from "../components/PlaceDetail.vue"; 
import PaymentPage from "../components/PaymentPage.vue";

const routes = [
  { path: "/", name: "Home", component: HomePage },
  { path: "/login", name: "Login", component: LoginForm },
  { path: "/register", name: "Register", component: RegisterForm },
  { path: "/mypage", name: "MyPage", component: MyPage },
  { path: "/profile", name: "Profile", component: UserProfile, meta: { requiresAuth: true } },
  { path: "/booking", name: "Booking", component: UserBooking, meta: { requiresAuth: true } },
  { path: "/sub-home/:category", name: "SubHome", component: SubHome },
  { path: "/sub-home/:category/:location", name: "LocationHome", component: LocationHome },
  { path: "/places/:accommodationId/:checkInDate/:checkOutDate", name: "PlaceDetail", component: Places },
  { path: "/payment", name: "PaymentPage", component: PaymentPage },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

router.beforeEach((to, from, next) => {
  console.log(`Navigating: from=${from.fullPath}, to=${to.fullPath}`);

  const queryParams = new URLSearchParams(window.location.search);
  const accessToken = queryParams.get("accessToken");
  const refreshToken = queryParams.get("refreshToken");
  const loginType = queryParams.get("loginType");

  if (accessToken) {
    console.log("accessToken 감지됨, 저장 시작");
    localStorage.setItem("accessToken", accessToken);
    localStorage.setItem("refreshToken", refreshToken);
    localStorage.setItem("loginType", loginType);
    
    if (to.path === "/login") {
      console.log(" 로그인 페이지에서 메인으로 리디렉트");
      return next("/");
    }

    return next();
  }

  const isLoggedIn = !!localStorage.getItem("accessToken");
  const requiresAuth = to.matched.some(record => record.meta.requiresAuth);

  if (requiresAuth && !isLoggedIn) {
    console.log(`인증 필요: ${to.fullPath} → 로그인 페이지로 이동`);
    sessionStorage.setItem("redirectUrl", to.fullPath);
    return next("/login");
  }

  console.log("정상 이동");
  next();
});

export default router;
