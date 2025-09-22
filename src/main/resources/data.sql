INSERT INTO role(name) VALUES ('USER');
INSERT INTO role(name) VALUES ('ADMIN');

-- Password: password123
insert into profile(email, nickname, gender, attraction, passion, password)
values ('alice@tecnocampus.cat', 'Alice', 'Woman', 'Woman', 'Music', '$2a$10$fVKfcc47q6lrNbeXangjYeY000dmjdjkdBxEOilqhapuTO5ZH0co2'),
       ('bob@tecnocampus.cat', 'Bob', 'Man', 'Woman', 'Dance', '$2a$10$fVKfcc47q6lrNbeXangjYeY000dmjdjkdBxEOilqhapuTO5ZH0co2'),
       ('carol@tecnocampus.cat', 'Carol', 'Woman', 'Man', 'Dance', '$2a$10$fVKfcc47q6lrNbeXangjYeY000dmjdjkdBxEOilqhapuTO5ZH0co2'),
       ('dave@tecnocampus.cat', 'Dave', 'Man', 'Woman', 'Dance', '$2a$10$fVKfcc47q6lrNbeXangjYeY000dmjdjkdBxEOilqhapuTO5ZH0co2'),
       ('eli@tecnocampus.cat', 'Eli', 'Woman', 'Man', 'Music', '$2a$10$fVKfcc47q6lrNbeXangjYeY000dmjdjkdBxEOilqhapuTO5ZH0co2'),
       ('bis@tecnocampus.cat', 'Bis', 'Bisexual', 'Bisexual', 'Music', '$2a$10$fVKfcc47q6lrNbeXangjYeY000dmjdjkdBxEOilqhapuTO5ZH0co2');

insert into like_profile(origin_profile_id, target_profile_id, creation_date, matched)
values (3, 4, current_date(), false); -- Carol likes Dave

insert into profile_roles(profile_id, role_id)
values (1, 2),
       (1, 1),
       (2, 1),
       (3, 1),
       (4, 1),
       (5, 1),
       (6, 1);
