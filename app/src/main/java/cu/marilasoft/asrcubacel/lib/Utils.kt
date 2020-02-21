package cu.marilasoft.asrcubacel.lib


fun convertFloatMoneyToString(credit: String): String {
    if (credit.contains(".")) {
        val size = credit.toCharArray().size
        var position = 0
        for (char in credit.toCharArray()) {
            if (char == '.') break
            position++
        }
        if (position + 2 == size) return "${credit}0 CUC"
        else if (position + 3 != size) return "${credit}.00 CUC"
    }
    return "$credit CUC"
}