package com.jhipsdemo.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.BooleanFilter;
import tech.jhipster.service.filter.DoubleFilter;
import tech.jhipster.service.filter.Filter;
import tech.jhipster.service.filter.FloatFilter;
import tech.jhipster.service.filter.IntegerFilter;
import tech.jhipster.service.filter.LocalDateFilter;
import tech.jhipster.service.filter.LongFilter;
import tech.jhipster.service.filter.StringFilter;

/**
 * Criteria class for the {@link com.jhipsdemo.domain.Transaction} entity. This class is used
 * in {@link com.jhipsdemo.web.rest.TransactionResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /transactions?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class TransactionCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter name;

    private StringFilter title;

    private IntegerFilter totalprice;

    private LocalDateFilter date;

    private StringFilter gender;

    public TransactionCriteria() {}

    public TransactionCriteria(TransactionCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.name = other.name == null ? null : other.name.copy();
        this.title = other.title == null ? null : other.title.copy();
        this.totalprice = other.totalprice == null ? null : other.totalprice.copy();
        this.date = other.date == null ? null : other.date.copy();
        this.gender = other.gender == null ? null : other.gender.copy();
    }

    @Override
    public TransactionCriteria copy() {
        return new TransactionCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public LongFilter id() {
        if (id == null) {
            id = new LongFilter();
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getName() {
        return name;
    }

    public StringFilter name() {
        if (name == null) {
            name = new StringFilter();
        }
        return name;
    }

    public void setName(StringFilter name) {
        this.name = name;
    }

    public StringFilter getTitle() {
        return title;
    }

    public StringFilter title() {
        if (title == null) {
            title = new StringFilter();
        }
        return title;
    }

    public void setTitle(StringFilter title) {
        this.title = title;
    }

    public IntegerFilter getTotalprice() {
        return totalprice;
    }

    public IntegerFilter totalprice() {
        if (totalprice == null) {
            totalprice = new IntegerFilter();
        }
        return totalprice;
    }

    public void setTotalprice(IntegerFilter totalprice) {
        this.totalprice = totalprice;
    }

    public LocalDateFilter getDate() {
        return date;
    }

    public LocalDateFilter date() {
        if (date == null) {
            date = new LocalDateFilter();
        }
        return date;
    }

    public void setDate(LocalDateFilter date) {
        this.date = date;
    }

    public StringFilter getGender() {
        return gender;
    }

    public StringFilter gender() {
        if (gender == null) {
            gender = new StringFilter();
        }
        return gender;
    }

    public void setGender(StringFilter gender) {
        this.gender = gender;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final TransactionCriteria that = (TransactionCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(name, that.name) &&
            Objects.equals(title, that.title) &&
            Objects.equals(totalprice, that.totalprice) &&
            Objects.equals(date, that.date) &&
            Objects.equals(gender, that.gender)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, title, totalprice, date, gender);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TransactionCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (name != null ? "name=" + name + ", " : "") +
            (title != null ? "title=" + title + ", " : "") +
            (totalprice != null ? "totalprice=" + totalprice + ", " : "") +
            (date != null ? "date=" + date + ", " : "") +
            (gender != null ? "gender=" + gender + ", " : "") +
            "}";
    }
}
