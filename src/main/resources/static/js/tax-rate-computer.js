new Vue({
  el: '#tax-rate-computer-app',
  data: {
    computed:               false,
    taxableIncome:          0,
    sanitizedTaxableIncome: 0,
    taxRate:                0
  },
  //watch: {
  //  taxableIncome: function() {
  //    // noinspection JSIgnoredPromiseFromCall
  //    this.computeTaxRate();
  //  }
  //},
  methods: {
    computeTaxRate: async function() {
      const response = await axios.get('/computeTaxRate', {params: {taxableIncome: this.taxableIncome}});
      this.sanitizedTaxableIncome = response.data.taxableIncome;
      this.taxRate                = response.data.taxRate;
      this.computed               = true;
    }
  }
});
Vue.config.devtools = true;
