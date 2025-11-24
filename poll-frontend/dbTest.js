import http from 'k6/http';

export const options = {
    vus: 1000,
    duration: '30s'
};

export default function () {

    const payload = JSON.stringify({
        "question": "Load test poll?",
        "publishedAt": "2025-01-01T00:00:00Z",
        "validUntil": "2025-01-08T00:00:00Z",
        "creatorId": 11111111,
        "options": [
            { "caption": "Java", "presentationOrder": 1 },
            { "caption": "Python", "presentationOrder": 2 }
        ]
    });

    const headers = {
        'Content-Type': 'application/json',
        'Cookie': 'JSESSIONID=6B3EB01EF045AAC61CD5E6C44ED43099'
    };

    // Riktig URL:
    const res = http.post('http://localhost:8080/polls', payload, { headers });

    console.log("Status:", res.status);
}
