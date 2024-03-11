import { Authority } from '@/shared/security/authority';
/* tslint:disable */
// prettier-ignore

// prettier-ignore
const Transaction = () => import('@/entities/transaction/transaction.vue');
// prettier-ignore
const TransactionUpdate = () => import('@/entities/transaction/transaction-update.vue');
// prettier-ignore
const TransactionDetails = () => import('@/entities/transaction/transaction-details.vue');
// prettier-ignore
const Book = () => import('@/entities/book/book.vue');
// prettier-ignore
const BookUpdate = () => import('@/entities/book/book-update.vue');
// prettier-ignore
const BookDetails = () => import('@/entities/book/book-details.vue');
// jhipster-needle-add-entity-to-router-import - JHipster will import entities to the router here

export default [
  {
    path: '/transaction',
    name: 'Transaction',
    component: Transaction,
    meta: { authorities: [Authority.USER] },
  },
  {
    path: '/transaction/new',
    name: 'TransactionCreate',
    component: TransactionUpdate,
    meta: { authorities: [Authority.USER] },
  },
  {
    path: '/transaction/:transactionId/edit',
    name: 'TransactionEdit',
    component: TransactionUpdate,
    meta: { authorities: [Authority.USER] },
  },
  {
    path: '/transaction/:transactionId/view',
    name: 'TransactionView',
    component: TransactionDetails,
    meta: { authorities: [Authority.USER] },
  },
  {
    path: '/book',
    name: 'Book',
    component: Book,
    meta: { authorities: [Authority.USER] },
  },
  {
    path: '/book/new',
    name: 'BookCreate',
    component: BookUpdate,
    meta: { authorities: [Authority.USER] },
  },
  {
    path: '/book/:bookId/edit',
    name: 'BookEdit',
    component: BookUpdate,
    meta: { authorities: [Authority.USER] },
  },
  {
    path: '/book/:bookId/view',
    name: 'BookView',
    component: BookDetails,
    meta: { authorities: [Authority.USER] },
  },
  // jhipster-needle-add-entity-to-router - JHipster will add entities to the router here
];
