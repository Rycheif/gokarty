-- DROP FUNCTION gokarty.random_string(integer)
CREATE OR REPLACE FUNCTION gokarty.random_string(length integer) RETURNS varchar AS
$$
DECLARE
    chars  varchar[] := '{0,1,2,3,4,5,6,7,8,9,A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P,Q,R,S,T,U,V,W,X,Y,Z,a,b,c,d,e,f,g,h,i,j,k,l,m,n,o,p,q,r,s,t,u,v,w,x,y,z}';
    result varchar   := '';
    i      integer   := 0;
BEGIN
    IF length < 0 THEN
        RAISE EXCEPTION 'Given length cannot be less than 0';
    END IF;
    FOR i IN 1..length
        LOOP
            result := result || chars[1 + RANDOM() * (ARRAY_LENGTH(chars, 1) - 1)];
        END LOOP;
    RETURN (SELECT crypt(result, gen_salt('md5')));
END;
$$ LANGUAGE plpgsql;
-- select gokarty.random_string(15)
-- -----------------------------------------------------------------
CREATE OR REPLACE FUNCTION gokarty.generate_app_role()
    RETURNS BOOLEAN AS
$GENERATOR$

BEGIN
    RAISE NOTICE 'Generuję dane dla tabeli APP_ROLE';
    INSERT INTO gokarty.app_role (name)
    VALUES ('ROLE_USER'),
           ('ROLE_EMPLOYEE'),
           ('ROLE_ADMIN');
    -- 	Koniec funkcji
    RETURN TRUE;
END;
$GENERATOR$
    LANGUAGE 'plpgsql';
-- -----------------------------------------------------------------
CREATE OR REPLACE FUNCTION gokarty.generate_app_user()
    RETURNS BOOLEAN AS
$GENERATOR$
DECLARE
    -- Zmienne modelu danych
    imie     CHARACTER VARYING(40);
    nazwisko CHARACTER VARYING(40);
    -- Zmienne pomocnicze
    i        int;
BEGIN

    RAISE NOTICE 'Generuję dane dla tabeli APP_USER';
    FOR i IN 1..5000
        LOOP

            SELECT imie.imie
            INTO STRICT imie
            FROM slownik.imie
            WHERE imie.id_imienia =
                  (SELECT TRUNC(1 + RANDOM() * (SELECT MAX(imie.id_imienia) FROM slownik.imie))::INTEGER);

            SELECT nazwisko.nazwisko
            INTO STRICT nazwisko
            FROM slownik.nazwisko
            WHERE nazwisko.id_nazwiska =
                  (SELECT TRUNC(1 + RANDOM() * (SELECT MAX(nazwisko.id_nazwiska) FROM slownik.nazwisko))::INTEGER);

            BEGIN
                INSERT INTO gokarty.app_user (id_app_user, name, phone, email, password, locked, enabled)
                VALUES (i,
                        imie || ' ' || nazwisko,
                        (SELECT '+48' || RPAD(TRUNC(RANDOM() * 10 ^ 9)::BIGINT::VARCHAR, 9, '0')),
                        LOWER(imie) || TRUNC(RANDOM() * 1000)::VARCHAR || '@' ||
                        (CASE TRUNC(RANDOM() * 10)::INTEGER
                             WHEN 0 THEN 'gmail.com'
                             WHEN 1 THEN 'onet.pl'
                             WHEN 2 THEN 'wp.pl'
                             WHEN 3 THEN 'interia.pl'
                             WHEN 4 THEN 'hotmail.com'
                             WHEN 5 THEN 'tarnow.pl'
                             WHEN 6 THEN 'apple.com'
                             WHEN 7 THEN 'samsung.com'
                             WHEN 8 THEN 'microsoft.com'
                             WHEN 9 THEN 'twitter.com'
                            END),
                        (SELECT gokarty.random_string(15)),
                        (SELECT (ARRAY [TRUE, FALSE])[TRUNC(1 + RANDOM() * 2)]),
                        (SELECT TRUE));
            EXCEPTION
                WHEN unique_violation THEN
                -- Nic nie rób. Spróbuj dodać kolejny rekord w pętli.
            END;

        END LOOP;
-- 	Koniec funkcji
    RETURN TRUE;
END;
$GENERATOR$
    LANGUAGE 'plpgsql';
-- -----------------------------------------------------------------
CREATE OR REPLACE FUNCTION gokarty.generate_app_user_role()
    RETURNS BOOLEAN AS
$GENERATOR$
DECLARE
    data  bigint[];
    i     int;
    losuj integer;
    l_pracownikow integer := 0;
BEGIN
    RAISE NOTICE 'Generuję dane dla tabeli APP_USER_ROLE';
    SELECT ARRAY(SELECT id_app_user FROM gokarty.app_user) INTO data;
    INSERT INTO gokarty.app_user_role(id_app_user, "id_app_role") VALUES (data[1], 3);
    INSERT INTO gokarty.app_user_role(id_app_user, "id_app_role") VALUES (data[2], 2);
    INSERT INTO gokarty.app_user_role(id_app_user, "id_app_role") VALUES (data[3], 2);
    INSERT INTO gokarty.app_user_role(id_app_user, "id_app_role") VALUES (data[4], 2);
    INSERT INTO gokarty.app_user_role(id_app_user, "id_app_role") VALUES (data[5], 2);
    INSERT INTO gokarty.app_user_role(id_app_user, "id_app_role") VALUES (data[6], 2);
    INSERT INTO gokarty.app_user_role(id_app_user, "id_app_role") VALUES (data[7], 2);
    INSERT INTO gokarty.app_user_role(id_app_user, "id_app_role") VALUES (data[8], 2);
    INSERT INTO gokarty.app_user_role(id_app_user, "id_app_role") VALUES (data[9], 2);
    FOR i IN 10..5000
        LOOP
            SELECT (1 + RANDOM() * 2)::int INTO losuj;
            INSERT INTO gokarty.app_user_role(id_app_user, "id_app_role")
            VALUES ((data[i]), 1);
        END LOOP;
    RETURN TRUE;
END;
$GENERATOR$
    LANGUAGE 'plpgsql';
-- -----------------------------------------------------------------
CREATE OR REPLACE FUNCTION gokarty.generate_email_confirmation_token()
    RETURNS BOOLEAN AS
$GENERATOR$
DECLARE
    -- Zmienne pomocnicze
    i      int;
    losowa timestamp;
BEGIN
    RAISE NOTICE 'Generuję dane dla tabeli EMAIL_CONFIRMATION_TOKEN';
    FOR i IN 1..5000
        LOOP
            losowa := '2010-06-22 19:10:25'::timestamp - TRUNC(RANDOM() * (365 * 1 + 1)) * '1 day'::INTERVAL - '1 day'::INTERVAL;
            INSERT INTO gokarty.email_confirmation_token (id_email_confirmation_token, token, created_at, expires_at,
                                                          confirmed_at, id_app_user)
            VALUES (i,
                    (SELECT gen_random_uuid()::VARCHAR),
                    (SELECT losowa),
                    (SELECT losowa + INTERVAL '1 day'),
                    ((SELECT losowa + INTERVAL '15 minutes')),
                    i);
        END LOOP;
-- 	Koniec funkcji
    RETURN TRUE;
END;
$GENERATOR$
    LANGUAGE 'plpgsql';
-- -----------------------------------------------------------------
CREATE OR REPLACE FUNCTION gokarty.generate_invoice()
    RETURNS BOOLEAN AS
$GENERATOR$
DECLARE
    data bigint[];
    i    int;
BEGIN
    RAISE NOTICE 'Generuję dane dla tabeli INVOICE';
    SELECT ARRAY(SELECT id_reservation FROM gokarty.reservation) INTO data;
    FOR i IN 1..5000
        LOOP
            INSERT INTO gokarty.invoice (id_reservation)
            VALUES (data[i]);
        END LOOP;
-- 	Koniec funkcji
    RETURN TRUE;
END;
$GENERATOR$
    LANGUAGE 'plpgsql';
-- -----------------------------------------------------------------
CREATE OR REPLACE FUNCTION gokarty.generate_kart()
    RETURNS BOOLEAN AS
$GENERATOR$

BEGIN
    RAISE NOTICE 'Generuję dane dla tabeli KART';
    INSERT INTO gokarty.kart (name, difficulty_level)
    VALUES ('Lightning McQueen', 'Hard'),
           ('Britney Steers', 'Easy'),
           ('Captain Amerikart', 'Easy'),
           ('Steervester Stallone', 'Medium'),
           ('Karty Perry', 'Medium'),
           ('Naskart', 'Hard'),
           ('Taylor Drift', 'Medium');
-- 	Koniec funkcji
    RETURN TRUE;
END;
$GENERATOR$
    LANGUAGE 'plpgsql';
-- -----------------------------------------------------------------
CREATE OR REPLACE FUNCTION gokarty.generate_reservation()
    RETURNS BOOLEAN AS
$GENERATOR$
DECLARE
    -- Zmienne pomocnicze
    i                 int;
    start_timestamp   TIMESTAMP;
    end_timestamp     TIMESTAMP;
    end_timestamp2    TIMESTAMP;
    random_timestamp  TIMESTAMP;
    random_timestamp2 TIMESTAMP;
    data              bigint[];
    random_value      int;
    money_value       numeric;
    minInterval       interval;

BEGIN
    RAISE NOTICE 'Generuję dane dla tabeli RESERVATION';
    minInterval = '30 minutes';
    start_timestamp = '2010-06-22 19:10:25'::timestamp;
    end_timestamp = '2010-06-22 19:10:25'::timestamp - INTERVAL '10 day' - INTERVAL '15 minutes';
    end_timestamp2 = '2010-06-22 19:10:25'::timestamp - INTERVAL '10 day' + INTERVAL '15 minutes';
    random_timestamp = start_timestamp + (minInterval + (end_timestamp - start_timestamp));
    random_timestamp2 = start_timestamp + (minInterval + (end_timestamp2 - start_timestamp));
    SELECT ARRAY(SELECT id_app_user FROM gokarty.app_user) INTO data;

    FOR i IN 1..5000
        LOOP
            SELECT (100 + RANDOM() * 450)::numeric INTO random_value;
            money_value := random_value::numeric;
            IF random_timestamp2 + minInterval >= CURRENT_TIMESTAMP
            THEN
                EXIT;
            ELSE
                INSERT INTO gokarty.reservation
                VALUES (i,
                        TSRANGE(random_timestamp + minInterval, random_timestamp2 + minInterval, '[]'),
                        (SELECT (1 + RANDOM() * 3)::int),
                        data[i],
                        (SELECT (1 + RANDOM() * 6)::int),
                        money_value);
                minInterval := minInterval + INTERVAL '31 minutes';
            END IF;
        END LOOP;
    RETURN TRUE;
END;
$GENERATOR$
    LANGUAGE 'plpgsql';
-- -----------------------------------------------------------------
CREATE OR REPLACE FUNCTION gokarty.generate_reservation_kart()
    RETURNS BOOLEAN AS
$GENERATOR$
DECLARE
    -- Zmienne pomocnicze
    i           int;
    kart        bigint[];
    periodRange tsrange[];
    track       bigint[];
    appUser     bigint[];
BEGIN
    RAISE NOTICE 'Generuję dane dla tabeli RESERVATION KART';
    SELECT ARRAY(SELECT id_kart FROM gokarty.kart) INTO kart;
    SELECT ARRAY(SELECT period FROM gokarty.reservation) INTO periodRange;
    SELECT ARRAY(SELECT id_track FROM gokarty.reservation) INTO track;
    SELECT ARRAY(SELECT id_app_user FROM gokarty.reservation) INTO appUser;
    FOR i IN 1..5000
        LOOP
            INSERT INTO gokarty.reservation_kart (id_kart, period, id_track, id_app_user)
            VALUES ((SELECT (1 + RANDOM() * 6)::int),
                    periodRange[i],
                    track[i],
                    appUser[i]);
        END LOOP;
-- 	Koniec funkcji
    RETURN TRUE;
END;
$GENERATOR$
    LANGUAGE 'plpgsql';
-- -----------------------------------------------------------------
CREATE OR REPLACE FUNCTION gokarty.generate_track()
    RETURNS BOOLEAN AS
$GENERATOR$

BEGIN
    RAISE NOTICE 'Generuję dane dla tabeli TRACK';
    INSERT INTO gokarty.track (length)
    VALUES (1000),
           (1250),
           (950),
           (1500);
-- 	Koniec funkcji
    RETURN TRUE;
END;
$GENERATOR$
    LANGUAGE 'plpgsql';
-- -----------------------------------------------------------------
CREATE OR REPLACE FUNCTION gokarty.generuj_dane()
    RETURNS BOOLEAN AS
$GENERATOR$
BEGIN
    -- Usunięcie istniejących danych z tabel
    DELETE FROM gokarty.email_confirmation_token;
    DELETE FROM gokarty.reservation_kart;
    DELETE FROM gokarty.invoice;
    DELETE FROM gokarty.reservation;
    DELETE FROM gokarty.app_user_role;
    DELETE FROM gokarty.app_role;
    DELETE FROM gokarty.app_user;
    DELETE FROM gokarty.kart;
    DELETE FROM gokarty.track;

    -- Ustawienie wartości sekwencji
    ALTER SEQUENCE gokarty."app_role_id_app_role_seq" RESTART WITH 1;
    ALTER SEQUENCE gokarty.app_user_id_app_user_seq RESTART WITH 1;
    ALTER SEQUENCE gokarty.email_confirmation_token_id_email_confirmation_token_seq RESTART WITH 1;
    ALTER SEQUENCE gokarty.invoice_id_invoice_seq RESTART WITH 1;
    ALTER SEQUENCE gokarty.kart_id_kart_seq RESTART WITH 1;
    ALTER SEQUENCE gokarty.track_id_track_seq RESTART WITH 1;

-- 	Wywolanie funkcji generujacych
    PERFORM * FROM gokarty.generate_app_role();
    PERFORM * FROM gokarty.generate_app_user();
    PERFORM * FROM gokarty.generate_app_user_role();
    PERFORM * FROM gokarty.generate_email_confirmation_token();
    PERFORM * FROM gokarty.generate_kart();
    PERFORM * FROM gokarty.generate_track();
    PERFORM * FROM gokarty.generate_reservation();
    PERFORM * FROM gokarty.generate_reservation_kart();
    PERFORM * FROM gokarty.generate_invoice();

-- 	Koniec funkcji
    RETURN TRUE;
END;

$GENERATOR$
    LANGUAGE 'plpgsql';

SELECT *
FROM gokarty.generuj_dane();
