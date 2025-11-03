// src/api/api.js
// Centralized Axios instance and API helpers for PollApp (session-based auth)
import axios from "axios";

// Axios instance that sends/receives JSESSIONID cookie
const api = axios.create({
    baseURL: "http://localhost:8080",
    withCredentials: true
});

/* Auth */
export const loginUser = (username, password) =>
    api.post("/auth/login", { username, password });

export const registerUser = (username, email, password) =>
    api.post("/auth/register", { username, email, password });

export const fetchCurrentUser = () => api.get("/auth/me");

export const logoutUser = () => api.post("/auth/logout");

/* Polls */
export const createPoll = (question, options) =>
    // Backend VoteOptionDto expects 'caption', not 'text'
    api.post("/polls", {
        question,
        options: options.map((o, i) => ({ caption: o, presentationOrder: i }))
    });

export const getPolls = () => api.get("/polls");

export const getPoll = (id) => api.get(`/polls/${id}`);

/* Votes */
export const voteOnPoll = (pollId, optionId, userId, value) =>
    // value: +1 (upvote) or -1 (downvote)
    api.post("/votes", { pollId, optionId, userId, value });

export const getVotesByPoll = (pollId) => api.get(`/polls/${pollId}/votes`);
// If you later expose aggregated results, you can use:
// export const getPollResults = (pollId) => api.get(`/polls/${pollId}/results`);

export default api;
