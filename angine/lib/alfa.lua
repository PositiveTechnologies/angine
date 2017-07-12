
local alfa = {}


local function test()
    print("test")
end


local function length(list)
    local i = 0
    if type(list) == "table" then
        for k,v in pairs(list) do
            i = i + 1
        end
        return i
    end
    if type(list) == "userdata" then
        local i = 0
        for item in python.iter(list) do
            i = i + 1
        end
        return i
    end
end


local function iselement(list, element)
    if type(list) == "table" then
        for k, v in pairs(list) do
            if v == element then
                return true
            end
        end
    end
    if type(list) == "userdata" then
        for item in python.iter(list) do
            if item == element then
                return true
            end
        end
    end
    return false
end


local function isany(list1, list2)
    if type(list1) == "table" then
        for k,v in pairs(list1) do
            if iselement(list2, v) then
                return true
            end
        end
        return false
    end
    if type(list1) == "userdata" then
        for item in python.iter(list1) do
            if iselement(list2, item) then
                return true
            end
        end
        return false
    end
end


local function issubset(list, sublist)
    if type(list) == "table" then
        for k,v in sublist do
            if not iselement(list, item) then
                return false
            end
        end
    end
    if type(list) == "userdata" then
        for item in python.iter(sublist) do
            if not iselement(list, item) then
                return false
            end
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
    isany = isany;
    issubset = issubset;
    issubseteq = issubseteq;
}
