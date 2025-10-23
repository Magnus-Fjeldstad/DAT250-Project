// src/api/votes.ts
import { http } from "./http";

export interface VoteDto {
    id?: number;
    pollId: number;
    userId: number;
    optionId: number;
    value: number;
}

export const VoteApi = {
    vote(vote: VoteDto): Promise<any> {
        return http.post("/votes", vote);
    },

    getAll(): Promise<VoteDto[]> {
        return http.get("/votes");
    },

    getByUser(userId: number): Promise<VoteDto[]> {
        return http.get(`/users/${userId}/votes`);
    },

    getByPoll(pollId: number): Promise<VoteDto[]> {
        return http.get(`/polls/${pollId}/votes`);
    },
};
