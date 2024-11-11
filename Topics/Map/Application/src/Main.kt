fun bill(priceList: Map<String, Int>, shoppingList: MutableList<String>): Int {
    return shoppingList.map { priceList.getOrDefault(it,0) }.sum()
}