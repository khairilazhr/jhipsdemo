import { Component, Vue, Inject } from 'vue-property-decorator';

import { required } from 'vuelidate/lib/validators';

import { ITransaction, Transaction } from '@/shared/model/transaction.model';
import TransactionService from './transaction.service';

const validations: any = {
  transaction: {
    name: {
      required,
    },
    title: {
      required,
    },
    totalprice: {
      required,
    },
    date: {
      required,
    },
    gender: {
      required,
    },
  },
};

@Component({
  validations,
})
export default class TransactionUpdate extends Vue {
  @Inject('transactionService') private transactionService: () => TransactionService;
  public transaction: ITransaction = new Transaction();
  public isSaving = false;
  public currentLanguage = '';

  beforeRouteEnter(to, from, next) {
    next(vm => {
      if (to.params.transactionId) {
        vm.retrieveTransaction(to.params.transactionId);
      }
    });
  }

  created(): void {
    this.currentLanguage = this.$store.getters.currentLanguage;
    this.$store.watch(
      () => this.$store.getters.currentLanguage,
      () => {
        this.currentLanguage = this.$store.getters.currentLanguage;
      }
    );
  }

  public save(): void {
    this.isSaving = true;
    if (this.transaction.id) {
      this.transactionService()
        .update(this.transaction)
        .then(param => {
          this.isSaving = false;
          this.$router.go(-1);
          const message = this.$t('jhipsdemoApp.transaction.updated', { param: param.id });
          return this.$root.$bvToast.toast(message.toString(), {
            toaster: 'b-toaster-top-center',
            title: 'Info',
            variant: 'info',
            solid: true,
            autoHideDelay: 5000,
          });
        });
    } else {
      this.transactionService()
        .create(this.transaction)
        .then(param => {
          this.isSaving = false;
          this.$router.go(-1);
          const message = this.$t('jhipsdemoApp.transaction.created', { param: param.id });
          this.$root.$bvToast.toast(message.toString(), {
            toaster: 'b-toaster-top-center',
            title: 'Success',
            variant: 'success',
            solid: true,
            autoHideDelay: 5000,
          });
        });
    }
  }

  public retrieveTransaction(transactionId): void {
    this.transactionService()
      .find(transactionId)
      .then(res => {
        this.transaction = res;
      });
  }

  public previousState(): void {
    this.$router.go(-1);
  }

  public initRelationships(): void {}
}
