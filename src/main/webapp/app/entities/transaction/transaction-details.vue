<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <div v-if="transaction">
        <h2 class="jh-entity-heading" data-cy="transactionDetailsHeading">
          <span v-text="$t('jhipsdemoApp.transaction.detail.title')">Transaction</span> {{ transaction.id }}
        </h2>
        <dl class="row jh-entity-details">
          <dt>
            <span v-text="$t('jhipsdemoApp.transaction.name')">Name</span>
          </dt>
          <dd>
            <span>{{ transaction.name }}</span>
          </dd>
          <dt>
            <span v-text="$t('jhipsdemoApp.transaction.title')">Title</span>
          </dt>
          <dd>
            <span>{{ transaction.title }}</span>
          </dd>
          <dt>
            <span v-text="$t('jhipsdemoApp.transaction.totalprice')">Totalprice</span>
          </dt>
          <dd>
            <span>{{ transaction.totalprice }}</span>
          </dd>
          <dt>
            <span v-text="$t('jhipsdemoApp.transaction.date')">Date</span>
          </dt>
          <dd>
            <span>{{ transaction.date }}</span>
          </dd>
          <dt>
            <span v-text="$t('jhipsdemoApp.transaction.gender')">Gender</span>
          </dt>
          <dd>
            <span>{{ transaction.gender }}</span>
          </dd>
        </dl>
        <button type="submit" v-on:click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span v-text="$t('entity.action.back')"> Back</span>
        </button>
        <router-link
          v-if="transaction.id"
          :to="{ name: 'TransactionEdit', params: { transactionId: transaction.id } }"
          custom
          v-slot="{ navigate }"
        >
          <button @click="navigate" class="btn btn-primary">
            <font-awesome-icon icon="pencil-alt"></font-awesome-icon>&nbsp;<span v-text="$t('entity.action.edit')"> Edit</span>
          </button>
        </router-link>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./transaction-details.component.ts"></script>
