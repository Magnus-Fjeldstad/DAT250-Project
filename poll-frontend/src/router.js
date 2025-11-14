// src/router.js
import { createRouter, createWebHistory } from "vue-router";
import Login from "./pages/Login.vue";
import Home from "./pages/Home.vue";
import api from "./api/api.js";
import CreatePoll from "./pages/CreatePoll.vue";

const requireAuth = async (next) => {
    try {
        await api.get("/auth/me");
        return true;
    } catch {
        return false;
    }
};

const routes = [
    { path: "/", component: Login },

    {
        path: "/home",
        component: Home,
        beforeEnter: async (to, from, next) => {
            if (await requireAuth()) next();
            else next("/public");
        }
    },

    {
        path: "/create-poll",
        component: CreatePoll,
        beforeEnter: async (to, from, next) => {
            if (await requireAuth()) next();
            else next("/public");
        }
    },

    {
        path: "/admin",
        component: () => import("./pages/AdminPage.vue"),
        // optional admin guard
        beforeEnter: async (to, from, next) => {
            try {
                const res = await api.get("/auth/me");
                if (res.data.role === "ROLE_ADMIN") next();
                else next("/public");
            } catch {
                next("/public");
            }
        }
    },

    {
        path: "/my-polls",
        component: () => import("./pages/MyPolls.vue"),
        beforeEnter: async (to, from, next) => {
            if (await requireAuth()) next();
            else next("/public");
        }
    },

    {
        path: "/public",
        name: "public",
        component: () => import("./pages/FallBack.vue")
    }
];

export default createRouter({
    history: createWebHistory(),
    routes
});
