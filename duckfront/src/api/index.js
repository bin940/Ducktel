import axios from "axios";
import router from "../router";

const api = axios.create({
  baseURL: "http://localhost:8090",
});


api.interceptors.request.use((config) => {
  const token = localStorage.getItem("accessToken");
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});


api.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config;
    if (error.response && error.response.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true; 
      const refreshToken = localStorage.getItem("refreshToken");
      
      if (refreshToken) {
        try {
          const { data } = await axios.post(
            "http://your-api-url/api/refresh", 
            { refreshToken }
          );
          const newAccessToken = data.accessToken;
          localStorage.setItem("accessToken", newAccessToken);
          originalRequest.headers.Authorization = `Bearer ${newAccessToken}`;
          return api(originalRequest);
        } catch (refreshError) {
          console.error("리프레시 토큰 실패:", refreshError);
          localStorage.removeItem("accessToken");
          localStorage.removeItem("refreshToken");
          alert("세션이 만료되었습니다. 다시 로그인해 주세요.");
          router.push("/login");
          return Promise.reject(refreshError);
        }
      } else {
        localStorage.removeItem("accessToken");
        localStorage.removeItem("refreshToken");
        alert("로그인 정보가 없습니다. 다시 로그인해 주세요.");
        router.push("/login");
      }
    }
    return Promise.reject(error);
  }
);

export default api;