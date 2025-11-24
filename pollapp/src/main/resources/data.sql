INSERT INTO users (id, username, email, password, role)
VALUES (
           11111111,
           'test5',
           'test5@example.com',
           '$2a$10$A2QL7pGb5fRhDxbdDv2cWeuRdh6ACkrGf3wH9nIGiyNJeY2wQOB82',
           'ROLE_USER'
       )
    ON CONFLICT (id) DO NOTHING;

INSERT INTO polls (id, question, published_at, valid_until, creator_id)
VALUES (
           9999,
           'What is your favorite programming language?',
           CURRENT_TIMESTAMP,
           CURRENT_TIMESTAMP + INTERVAL '7 days',
           11111111
       )
    ON CONFLICT (id) DO NOTHING;

INSERT INTO vote_options (id, caption, presentation_order, poll_id)
VALUES
    (99991, 'Java', 1, 9999),
    (99992, 'Python', 2, 9999),
    (99993, 'Go', 3, 9999),
    (99994, 'Rust', 4, 9999)
    ON CONFLICT (id) DO NOTHING;
