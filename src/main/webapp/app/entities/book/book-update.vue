<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <form name="editForm" role="form" novalidate v-on:submit.prevent="save()">
        <h2
          id="jhipsdemoApp.book.home.createOrEditLabel"
          data-cy="BookCreateUpdateHeading"
          v-text="$t('jhipsdemoApp.book.home.createOrEditLabel')"
        >
          Create or edit a Book
        </h2>
        <div>
          <div class="form-group" v-if="book.id">
            <label for="id" v-text="$t('global.field.id')">ID</label>
            <input type="text" class="form-control" id="id" name="id" v-model="book.id" readonly />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="$t('jhipsdemoApp.book.title')" for="book-title">Title</label>
            <input
              type="text"
              class="form-control"
              name="title"
              id="book-title"
              data-cy="title"
              :class="{ valid: !$v.book.title.$invalid, invalid: $v.book.title.$invalid }"
              v-model="$v.book.title.$model"
              required
            />
            <div v-if="$v.book.title.$anyDirty && $v.book.title.$invalid">
              <small class="form-text text-danger" v-if="!$v.book.title.required" v-text="$t('entity.validation.required')">
                This field is required.
              </small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="$t('jhipsdemoApp.book.price')" for="book-price">Price</label>
            <input
              type="number"
              class="form-control"
              name="price"
              id="book-price"
              data-cy="price"
              :class="{ valid: !$v.book.price.$invalid, invalid: $v.book.price.$invalid }"
              v-model.number="$v.book.price.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="$t('jhipsdemoApp.book.publicationDate')" for="book-publicationDate"
              >Publication Date</label
            >
            <b-input-group class="mb-3">
              <b-input-group-prepend>
                <b-form-datepicker
                  aria-controls="book-publicationDate"
                  v-model="$v.book.publicationDate.$model"
                  name="publicationDate"
                  class="form-control"
                  :locale="currentLanguage"
                  button-only
                  today-button
                  reset-button
                  close-button
                >
                </b-form-datepicker>
              </b-input-group-prepend>
              <b-form-input
                id="book-publicationDate"
                data-cy="publicationDate"
                type="text"
                class="form-control"
                name="publicationDate"
                :class="{ valid: !$v.book.publicationDate.$invalid, invalid: $v.book.publicationDate.$invalid }"
                v-model="$v.book.publicationDate.$model"
              />
            </b-input-group>
          </div>
        </div>
        <div>
          <button type="button" id="cancel-save" class="btn btn-secondary" v-on:click="previousState()">
            <font-awesome-icon icon="ban"></font-awesome-icon>&nbsp;<span v-text="$t('entity.action.cancel')">Cancel</span>
          </button>
          <button
            type="submit"
            id="save-entity"
            data-cy="entityCreateSaveButton"
            :disabled="$v.book.$invalid || isSaving"
            class="btn btn-primary"
          >
            <font-awesome-icon icon="save"></font-awesome-icon>&nbsp;<span v-text="$t('entity.action.save')">Save</span>
          </button>
        </div>
      </form>
    </div>
  </div>
</template>
<script lang="ts" src="./book-update.component.ts"></script>
