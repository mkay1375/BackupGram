const Utils = {
    executeAsync: function (func) {
        setTimeout(func, 0);
    },
    convertToJalaliDate: function (tlDate) {
        let gregorianDate = new Date(tlDate * 1000);
        let jalaliDate = ginj(gregorianDate.getFullYear(), gregorianDate.getMonth() + 1, gregorianDate.getDate(), true)
            + ' ' + this.fixNumberLength(gregorianDate.getHours(), 2) + ':' + this.fixNumberLength(gregorianDate.getMinutes(), 2);
        return jalaliDate;
    },
    deparam: function (params) {
        let obj = {}, pair;
        params.split('&').forEach(param => {
            pair = param.split('=');
            obj[pair[0]] = isNaN(pair[1]) ? pair[1] : +pair[1];
        });
        return obj;
    },
    refreshPageWithNewHash: function (hash) {
        location.replace(location.href.split('#')[0] + '#' + hash);location.reload();
    },
    fixNumberLength: function (number, length) {
        let sNumber = number.toString();
        if (sNumber.length >= length)
            return sNumber;

        let zeros = '';
        for (let i = 0; i < length - sNumber.length; i++) {
            zeros += '0';
        }
        return zeros + sNumber;
    }
};