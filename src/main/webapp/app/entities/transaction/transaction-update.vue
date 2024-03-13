<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <form name="editForm" role="form" novalidate v-on:submit.prevent="save()">
        <h2
          id="jhipsdemoApp.transaction.home.createOrEditLabel"
          data-cy="TransactionCreateUpdateHeading"
          v-text="$t('jhipsdemoApp.transaction.home.createOrEditLabel')"
        >
          Create or edit a Transaction
        </h2>
        <div>
          <div class="form-group" v-if="transaction.id">
            <label for="id" v-text="$t('global.field.id')">ID</label>
            <input type="text" class="form-control" id="id" name="id" v-model="transaction.id" readonly />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="$t('jhipsdemoApp.transaction.name')" for="transaction-name">Name</label>
            <input
              type="text"
              class="form-control"
              name="name"
              id="transaction-name"
              data-cy="name"
              :class="{ valid: !$v.transaction.name.$invalid, invalid: $v.transaction.name.$invalid }"
              v-model="$v.transaction.name.$model"
              required
            />
            <div v-if="$v.transaction.name.$anyDirty && $v.transaction.name.$invalid">
              <small class="form-text text-danger" v-if="!$v.transaction.name.required" v-text="$t('entity.validation.required')">
                This field is required.
              </small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="$t('jhipsdemoApp.transaction.title')" for="transaction-title">Title</label>
            <input
              type="text"
              class="form-control"
              name="title"
              id="transaction-title"
              data-cy="title"
              :class="{ valid: !$v.transaction.title.$invalid, invalid: $v.transaction.title.$invalid }"
              v-model="$v.transaction.title.$model"
              required
            />
            <div v-if="$v.transaction.title.$anyDirty && $v.transaction.title.$invalid">
              <small class="form-text text-danger" v-if="!$v.transaction.title.required" v-text="$t('entity.validation.required')">
                This field is required.
              </small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="$t('jhipsdemoApp.transaction.totalprice')" for="transaction-totalprice"
              >Totalprice</label
            >
            <input
              type="number"
              class="form-control"
              name="totalprice"
              id="transaction-totalprice"
              data-cy="totalprice"
              :class="{ valid: !$v.transaction.totalprice.$invalid, invalid: $v.transaction.totalprice.$invalid }"
              v-model.number="$v.transaction.totalprice.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="$t('jhipsdemoApp.transaction.date')" for="transaction-date">Date</label>
            <b-input-group class="mb-3">
              <b-input-group-prepend>
                <b-form-datepicker
                  aria-controls="transaction-date"
                  v-model="$v.transaction.date.$model"
                  name="date"
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
                id="transaction-date"
                data-cy="date"
                type="text"
                class="form-control"
                name="date"
                :class="{ valid: !$v.transaction.date.$invalid, invalid: $v.transaction.date.$invalid }"
                v-model="$v.transaction.date.$model"
              />
            </b-input-group>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="$t('jhipsdemoApp.transaction.gender')" for="transaction-gender">Gender</label>
            <input
              type="text"
              class="form-control"
              name="gender"
              id="transaction-gender"
              data-cy="gender"
              :class="{ valid: !$v.transaction.gender.$invalid, invalid: $v.transaction.gender.$invalid }"
              v-model="$v.transaction.gender.$model"
            />
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
            :disabled="$v.transaction.$invalid || isSaving"
            class="btn btn-primary"
          >
            <font-awesome-icon icon="save"></font-awesome-icon>&nbsp;<span v-text="$t('entity.action.save')">Save</span>
          </button>
        </div>
      </form>
    </div>
  </div>
</template>
<script lang="ts" src="./transaction-update.component.ts"></script>
