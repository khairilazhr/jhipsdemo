import { mixins } from 'vue-class-component';

import { Component, Vue, Inject } from 'vue-property-decorator';
import Vue2Filters from 'vue2-filters';
import { ITransaction } from '@/shared/model/transaction.model';

import TransactionService from './transaction.service';

@Component({
  mixins: [Vue2Filters.mixin],
})
export default class Transaction extends Vue {
  @Inject('transactionService') private transactionService: () => TransactionService;
  private removeId: number = null;
  public itemsPerPage = 20;
  public queryCount: number = null;
  public page = 1;
  public previousPage = 1;
  public propOrder = 'id';
  public reverse = false;
  public totalItems = 0;
  public freeText = '';
  public searchQuery = '';

  public transactions: ITransaction[] = [];

  public isFetching = false;

  public mounted(): void {
    this.retrieveAllTransactions();
  }

  public clear(): void {
    this.page = 1;
    this.retrieveAllTransactions();
  }

  public search(freeText): void {
    this.freeText = freeText;
    this.retrieveAllTransactions();
  }

  public retrieveAllTransactions(): void {
    this.isFetching = true;

    const paginationQuery = {
      page: this.page - 1,
      size: this.itemsPerPage,
      sort: this.sort(),
    };

    if (this.freeText) {
      this.searchQuery = 'name.contains=' + this.freeText;
    }

    this.transactionService()
      .retrieve(paginationQuery, this.searchQuery)
      .then(
        res => {
          this.transactions = res.data;
          this.totalItems = Number(res.headers['x-total-count']);
          this.queryCount = this.totalItems;
          this.isFetching = false;
        },
        err => {
          this.isFetching = false;
        }
      );
  }

  public handleSyncList(): void {
    this.clear();
  }

  public prepareRemove(instance: ITransaction): void {
    this.removeId = instance.id;
    if (<any>this.$refs.removeEntity) {
      (<any>this.$refs.removeEntity).show();
    }
  }

  public removeTransaction(): void {
    this.transactionService()
      .delete(this.removeId)
      .then(() => {
        const message = this.$t('jhipsdemoApp.transaction.deleted', { param: this.removeId });
        this.$bvToast.toast(message.toString(), {
          toaster: 'b-toaster-top-center',
          title: 'Info',
          variant: 'danger',
          solid: true,
          autoHideDelay: 5000,
        });
        this.removeId = null;
        this.retrieveAllTransactions();
        this.closeDialog();
      });
  }

  public sort(): Array<any> {
    const result = [this.propOrder + ',' + (this.reverse ? 'desc' : 'asc')];
    if (this.propOrder !== 'id') {
      result.push('id');
    }
    return result;
  }

  public loadPage(page: number): void {
    if (page !== this.previousPage) {
      this.previousPage = page;
      this.transition();
    }
  }

  public transition(): void {
    this.retrieveAllTransactions();
  }

  public changeOrder(propOrder): void {
    this.propOrder = propOrder;
    this.reverse = !this.reverse;
    this.transition();
  }

  public closeDialog(): void {
    (<any>this.$refs.removeEntity).hide();
  }
}
