import axios from 'axios';
import {refreshToken} from "./userUtils";
import {getCookie} from "./cookiesManager";

const axiosInstance = axios.create({
    baseURL: 'http://localhost:8082'
})
axiosInstance.interceptors.response.use(
    (response) => {
        return response
    },
    async (error) => {
        if (error.response) {
            const originalConfig = error.config
            if (error.response.status === 401 && !originalConfig._retry) {
                originalConfig._retry = true;
                if (await refreshToken())
                    return axiosInstance(originalConfig)
            }
        }
        return Promise.reject(error)
    }
);
axiosInstance.interceptors.request.use(
    async (config) => {
        const accessToken = getCookie("accessToken")

        if (!config.headers['Content-Type'])
            config.headers['Content-Type'] = 'application/json'
        if (accessToken) {
            if (!config.headers['Authorization'])
                config.headers['Authorization'] = `Bearer ${accessToken}`
        }
        return config
    },
    async (error) => {
        return Promise.reject(error)
    }
);

export default axiosInstance;