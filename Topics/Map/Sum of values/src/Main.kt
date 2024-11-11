fun summator(map: Map<Int, Int>): Int {
    return map.keys.filter { it % 2 == 0 }.sumOf { map[it]!! }
}