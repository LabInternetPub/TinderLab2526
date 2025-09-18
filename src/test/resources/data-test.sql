insert into profile(email, nickname, gender, attraction, passion)
values ('alice@tecnocampus.cat', 'Alice', 'Woman', 'Woman', 'Music'),
       ('bob@tecnocampus.cat', 'Bob', 'Man', 'Woman', 'Dance'),
       ('carol@tecnocampus.cat', 'Carol', 'Woman', 'Man', 'Dance'),
       ('dave@tecnocampus.cat', 'Dave', 'Man', 'Woman', 'Dance'),
       ('eli@tecnocampus.cat', 'Eli', 'Woman', 'Woman', 'Music');

insert into like_profile(origin_profile_id, target_profile_id, creation_date, matched)
values (3, 4, current_date(), false); -- Carol likes Dave
