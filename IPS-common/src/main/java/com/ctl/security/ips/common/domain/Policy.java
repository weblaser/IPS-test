package com.ctl.security.ips.common.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Created by Kevin.Weber on 10/27/2014.
 */
public class Policy {

    private String id;
    private PolicyStatus status;

    public String getId() {
        return id;
    }

    public Policy setId(String id) {
        this.id = id;
        return this;
    }

    public PolicyStatus getStatus() {
        return status;
    }

    public Policy setStatus(PolicyStatus status) {
        this.status = status;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (o == this) {
            return true;
        }
        if (o.getClass() != getClass()) {
            return false;
        }
        Policy rhs = (Policy) o;
        return new EqualsBuilder()
                .append(id, rhs.id)
                .append(status, rhs.status)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(id)
                .append(status)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id", id)
                .append("status", status)
                .toString();
    }

}
