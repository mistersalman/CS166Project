CREATE SEQUENCE passengerID_seq START WITH SELECT pid from passenger WHERE pid >= ALL (Select pid from passenger);
CREATE SEQUENCE reviewID START WITH Select rid from ratings where rid >= (Select rid from ratings);



CREATE LANGUAGE plpgsql;
CREATE OR REPLACE FUNCTION passengerID ( )
Returns "trigger" AS '
  BEGIN
    NEW.part_number = nextval(''passengerID_seq'');
    RETURN NEW;
  END;
' LANGUAGE 'plpgsql' VOLATILE;
DROP TRIGGER IF EXISTS getPassengerID ON passenger;
CREATE TRIGGER getPassengerID BEFORE INSERT ON passenger FOR EACH ROW Execute PROCEDURE passengerID ( );


CREATE LANGUAGE plpgsql;
CREATE OR REPLACE FUNCTION reviewID ( )
Returns "trigger" AS '
  BEGIN
    NEW.part_number = nextval(''reviewID_seq'');
    RETURN NEW;
  END;
' LANGUAGE 'plpgsql' VOLATILE;
DROP TRIGGER IF EXISTS getReviewID ON reatings;
CREATE TRIGGER getReviewID BEFORE INSERT ON ratings FOR EACH ROW Execute PROCEDURE reviewID ( );

