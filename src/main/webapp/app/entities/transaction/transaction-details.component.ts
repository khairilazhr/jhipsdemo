import { Component, Vue, Inject } from 'vue-property-decorator';

import { ITransaction } from '@/shared/model/transaction.model';
import TransactionService from './transaction.service';

@Component
export default class TransactionDetails extends Vue {
  @Inject('transactionService') private transactionService: () => TransactionService;
  public transaction: ITransaction = {};

  beforeRouteEnter(to, from, next) {
    next(vm => {
      if (to.params.transactionId) {
        vm.retrieveTransaction(to.params.transactionId);
      }
    });
  }

  public retrieveTransaction(transactionId) {
    this.transactionService()
      .find(transactionId)
      .then(res => {
        this.transaction = res;
      });
  }

  public previousState() {
    this.$router.go(-1);
  }
}
