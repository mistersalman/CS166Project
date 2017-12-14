CREATE SEQUENCE passengerID_seq START WITH 1;
SELECT setval('passengerID_seq', COALESCE((SELECT pid+1 FROM passenger WHERE pid >= ALL (Select pid from passenger)), 1), false);
CREATE SEQUENCE reviewID_seq START WITH 1;
SELECT setval('reviewID_seq', COALESCE((Select rid+1 from ratings where rid >= ALL (Select rid from ratings)), 1), false);

CREATE OR REPLACE FUNCTION passengerID ( )
Returns "trigger" AS '
  BEGIN
    NEW.pid = nextval(''passengerID_seq'');
    RETURN NEW;
  END;
' LANGUAGE 'plpgsql' VOLATILE;
DROP TRIGGER IF EXISTS getPassengerID ON passenger;
CREATE TRIGGER getPassengerID BEFORE INSERT ON passenger FOR EACH ROW Execute PROCEDURE passengerID ( );

CREATE OR REPLACE FUNCTION reviewID ( )
Returns "trigger" AS '
  BEGIN
    NEW.rid = nextval(''reviewID_seq'');
    RETURN NEW;
  END;
' LANGUAGE 'plpgsql' VOLATILE;
DROP TRIGGER IF EXISTS getReviewID ON ratings;
CREATE TRIGGER getReviewID BEFORE INSERT ON ratings FOR EACH ROW Execute PROCEDURE reviewID ( );

CREATE INDEX flight_num
ON flight
USING BTREE (flightnum);

alter table flight CLUSTER ON flight_num;
cluster flight;

CREATE INDEX passenger_id
ON passenger
USING BTREE (pid);

alter table passenger CLUSTER ON passenger_id;
cluster passenger;

CREATE INDEX air_id
ON airline
USING BTREE (airid);

alter table airline CLUSTER ON air_id;
cluster airline;

CREATE INDEX flight_num_booking
ON booking
USING BTREE (flightnum);

alter table booking CLUSTER ON flight_num_booking;
cluster booking;
