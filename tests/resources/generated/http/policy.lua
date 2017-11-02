local lib = require('angine/alfa')
local __test = lib.test
local __subseteq = lib.subseteq
local __issubseteq = lib.issubseteq
local __iselement = lib.iselement
local __isany = lib.isany

-- example namespace begin
local example = {}
local __example = function(example)
    -- Main policy begin
    function example.Main(ctx, actions, handlers)
        -- target begin
        if not ctx.action then
            return actions.indeterminate
        end
        if not ( ctx.action == "GET" ) then
            return actions.notapplicable
        end
        -- target end
        
        -- r1 rule begin
        local function r1(ctx, actions, handlers)
            if not ctx.entity.level or not ctx.subject.level then
                return actions.indeterminate
            end
            if ( ctx.subject.level > ctx.entity.level ) then
                return actions.permit
            end
            return actions.notapplicable
        end
        -- r1 rule end
        
        
        -- r2 rule begin
        local function r2(ctx, actions, handlers)
            if not ctx.entity.path then
                return actions.indeterminate
            end
            if ( ctx.entity.path == "/admin/" ) then
                return actions.deny
            end
            return actions.notapplicable
        end
        -- r2 rule end
        
        
        -- r3 rule begin
        local function r3(ctx, actions, handlers)
            if not ctx.subject.ip then
                return actions.indeterminate
            end
            if ( ctx.subject.ip == "127.0.0.1" ) then
                return actions.deny
            end
            return actions.notapplicable
        end
        -- r3 rule end
        
        
        -- denyoverrides rule-combining algorithm begin
        local atLeastOneError = false
        local atLeastOnePermit = false
        local rules = { r0 = r1, r1 = r2, r2 = r3 }
        for _, rule in pairs(rules) do
            local decision = rule(ctx, actions, handlers)
            if decision == actions.deny then
                return actions.deny
            end
            if decision == actions.permit then
                atLeastOnePermit = true
            end
            if decision == actions.indeterminate then
                atLeastOneError = true
            end
        end
        if atLeastOneError == true then
            return actions.indeterminate
        end
        if atLeastOnePermit == true then
            return actions.permit
        end
        return actions.notapplicable
        -- denyoverrides rule-combining algorithm end
        
    end
    -- example.Main policy end
    
end
__example(example)
-- example namespace end

function __main(ctx, actions, handlers)
    return example.Main(ctx, actions, handlers)
end
