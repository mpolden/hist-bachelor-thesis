package models;

import play.db.jpa.Model;

import javax.persistence.Column;
import javax.persistence.Entity;

/**
 * This model class represents a user
 */
@Entity
public class User extends Model {

    // XXX: Only deviceId for now, probably needs username/password since
    // deviceId can change
    @Column(unique = true)
    public String deviceId;
}
