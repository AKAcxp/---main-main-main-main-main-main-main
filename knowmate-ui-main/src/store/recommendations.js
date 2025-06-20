import { defineStore } from "pinia";
import axios from "axios";

export const useRecommendationStore = defineStore("recommendation", {
  state: () => ({
    recommendations: [],
    loading: false,
    error: null,
  }),
  actions: {
    setRecommendations(data) {
      this.recommendations = data;
    },
    setLoading(status) {
      this.loading = status;
    },
    setError(error) {
      this.error = error;
    },
    clearRecommendations() {
      this.recommendations = [];
      this.error = null;
    },
    async fetchRecommendations() {
      this.setLoading(true);
      this.setError(null);
      try {
        const response = await axios.get("/api/recommendation");
        if (response.data.code === 200 && response.data.data) {
          this.setRecommendations(response.data.data);
        } else {
          this.setError(response.data.message || "获取推荐数据失败");
          this.setRecommendations([]);
        }
      } catch (err) {
        console.error("Error fetching recommendations:", err);
        this.setError("网络请求失败或服务器错误");
        this.setRecommendations([]);
      } finally {
        this.setLoading(false);
      }
    },
  },
});
