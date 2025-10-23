package com.example.pollapp.service;

import com.example.pollapp.dto.PollResultDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import redis.clients.jedis.JedisPooled;
import redis.clients.jedis.resps.Tuple;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class PollResultCacheService {

    private final JedisPooled jedis;
    private static final int TTL_SECONDS = 600;

    private String zsetKey(Long pollId) { return "poll:%d:opts".formatted(pollId); }
    private String optKey(Long pollId, Long optionId) { return "poll:%d:opt:%d".formatted(pollId, optionId); }

    /**
     * Lagrer hele resultatlisten (en entry per option) og setter TTL.
     * Bruker:
     * - Sorted set:   poll:<pollId>:opts   (member=optionId, score=presentationOrder)
     * - Hash per opt: poll:<pollId>:opt:<optionId>  fields: caption, up, down
     */
    public void saveResults(Long pollId, List<PollResultDto> results, Map<Long, Integer> orders) {
        String opts = zsetKey(pollId);

        Map<String, Double> members = new HashMap<>();
        for (PollResultDto r : results) {
            int order = orders.getOrDefault(r.getOptionId(), 0);
            members.put(String.valueOf(r.getOptionId()), (double) order);
        }
        if (!members.isEmpty()) {
            jedis.zadd(opts, members);
        }

        for (PollResultDto r : results) {
            String hk = optKey(pollId, r.getOptionId());
            Map<String, String> map = new HashMap<>();
            map.put("caption", r.getCaption() == null ? "" : r.getCaption());
            map.put("up", String.valueOf(Optional.ofNullable(r.getUpVotes()).orElse(0)));
            map.put("down", String.valueOf(Optional.ofNullable(r.getDownVotes()).orElse(0)));
            jedis.hset(hk, map);
            jedis.expire(hk, TTL_SECONDS);
        }

        jedis.expire(opts, TTL_SECONDS);
    }

    /**
     * Leser resultatlisten fra cache. Returnerer Optional.empty() ved cache-miss.
     */
    public Optional<List<PollResultDto>> loadResults(Long pollId) {
        String opts = zsetKey(pollId);

        List<Tuple> ordered = jedis.zrangeWithScores(opts, 0, -1);
        if (ordered == null || ordered.isEmpty()) {
            return Optional.empty();
        }


        List<Long> optionIds = ordered.stream()
                .map(t -> Long.valueOf(t.getElement()))
                .toList();

        List<PollResultDto> results = new ArrayList<>();
        for (Long optionId : optionIds) {
            String hk = optKey(pollId, optionId);
            Map<String, String> map = jedis.hgetAll(hk);

            if (map == null || map.isEmpty()) {
                return Optional.empty();
            }

            String caption = map.getOrDefault("caption", "");
            int up = parseInt(map.get("up"));
            int down = parseInt(map.get("down"));

            results.add(PollResultDto.builder()
                    .optionId(optionId)
                    .caption(caption)
                    .upVotes(up)
                    .downVotes(down)
                    .build());
        }
        return Optional.of(results);
    }



    public void evict(Long pollId) {
        String opts = zsetKey(pollId);
        List<String> ids = jedis.zrange(opts, 0, -1);
        if (ids != null) {
            for (String id : ids) {
                jedis.del(optKey(pollId, Long.valueOf(id)));
            }
        }
        jedis.del(opts);
    }

    private int parseInt(String s) {
        try { return s == null ? 0 : Integer.parseInt(s); }
        catch (NumberFormatException e) { return 0; }
    }
}
