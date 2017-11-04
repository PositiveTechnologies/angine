local alfa = {}

local function test()
    print("test")
end


local function length(list)
    local i = 0
    for k,v in pairs(list) do
        i = i + 1
    end
    return i
end


local function iselement(list, element)
    for k, v in pairs(list) do
        if v == element then
            return true
        end
    end
    return false
end


local function issubset(list, sublist)
    for k,v in sublist do
        if not iselement(list, item) then
            return false
        end
    end
    return true
end


local function issubseteq(list, sublist)
    if length(list) ~= length(sublist) then
        return false
    end
    return issubset(list, sublist)
end


return {
    test = test;
    length = length;
    iselement = iselement;
    issubset = issubset;
    issubseteq = issubseteq;
}