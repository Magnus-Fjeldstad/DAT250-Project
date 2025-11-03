// src/api.js
import axios from "axios";

// axios instance with JSESSIONID cookie support
const api = axios.create({
    baseURL: "http://localhost:8080",
    withCredentials: true
});

// login request
export const loginUser = (username, password) =>
    api.post("/auth/login", { username, password });

// register request
export const registerUser = (username, email, password) =>
    api.post("/auth/register", { username, email, password });

// check session
export const fetchCurrentUser = () =>
    api.get("/auth/me");

// logout
export const logoutUser = () =>
    api.post("/auth/logout");

export const createPoll = (question, options) =>
    api.post("/polls", {
        question,
        options: options.map(o => ({ text: o }))
    });

// fetch all polls
export const getPolls = () =>
    api.get("/polls");


export default api;
