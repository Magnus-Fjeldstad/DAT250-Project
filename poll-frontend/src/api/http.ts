// src/api/http.ts
const BASE_URL = "http://localhost:8080"; // endre ved behov

async function request(path: string, options: RequestInit = {}) {
    console.log("[HTTP] →", path, options);

    const response = await fetch(BASE_URL + path, {
        headers: {
            "Content-Type": "application/json",
        },
        ...options,
    });

    let body: any = null;
    try {
        // prøv å parse json, hvis ikke: ren tekst
        const text = await response.text();
        try {
            body = JSON.parse(text);
        } catch {
            body = text;
        }
    } catch {
        body = null;
    }

    console.log("[HTTP] ←", response.status, body);

    if (!response.ok && response.status !== 204) {
        throw new Error(`HTTP error: ${response.status} - ${JSON.stringify(body)}`);
    }

    return body;
}


export const http = {
    get: <T>(path: string): Promise<T> =>
        request(path, { method: "GET" }),

    post: <T>(path: string, body: any): Promise<T> =>
        request(path, { method: "POST", body: JSON.stringify(body) }),

    put: <T>(path: string, body: any): Promise<T> =>
        request(path, { method: "PUT", body: JSON.stringify(body) }),

    delete: (path: string): Promise<void> =>
        request(path, { method: "DELETE" }),
};
