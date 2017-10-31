local lib = require('alfa')
local __test = lib.test
local __subseteq = lib.subseteq
local __issubseteq = lib.issubseteq
local __iselement = lib.iselement

-- example namespace begin
local example = {}
local __example = function(example)
    -- mainPolicy policy set begin
    function example.mainPolicy(ctx, actions, handlers)
        -- getIndex policy begin
        local function getIndex(ctx, actions, handlers)
            -- target begin
            if not ctx.entity.path or not ctx.action then
                return actions.indeterminate
            end
            if not ( ctx.action == "GET" and ctx.entity.path == "/index.html" ) then
                return actions.notapplicable
            end
            -- target end
            
            -- r1 rule begin
            local function r1(ctx, actions, handlers)
                if not ctx.subject.role then
                    return actions.indeterminate
                end
                if ( __iselement({"user", "admin", "guest"}, ctx.subject.role) ) then
                    return actions.permit
                end
                return actions.notapplicable
            end
            -- r1 rule end
            
            
            -- denyunlesspermit rule-combining algorithm begin
            local rules = { r0 = r1 }
            for _, rule in pairs(rules) do
                local decision = rule(ctx, actions, handlers)
                if decision == actions.permit then
                    return actions.permit
                end
            end
            return actions.deny
            -- denyunlesspermit rule-combining algorithm end
            
        end
        -- getIndex policy end
        
        -- getMotd policy begin
        local function getMotd(ctx, actions, handlers)
            -- target begin
            if not ctx.entity.path or not ctx.action then
                return actions.indeterminate
            end
            if not ( ctx.action == "GET" and ctx.entity.path == "/motd" ) then
                return actions.notapplicable
            end
            -- target end
            
            -- r1 rule begin
            local function r1(ctx, actions, handlers)
                if not ctx.subject.role then
                    return actions.indeterminate
                end
                if ( __iselement({"user", "admin"}, ctx.subject.role) ) then
                    return actions.permit
                end
                return actions.notapplicable
            end
            -- r1 rule end
            
            
            -- denyunlesspermit rule-combining algorithm begin
            local rules = { r0 = r1 }
            for _, rule in pairs(rules) do
                local decision = rule(ctx, actions, handlers)
                if decision == actions.permit then
                    return actions.permit
                end
            end
            return actions.deny
            -- denyunlesspermit rule-combining algorithm end
            
        end
        -- getMotd policy end
        
        -- postMotd policy begin
        local function postMotd(ctx, actions, handlers)
            -- target begin
            if not ctx.entity.path or not ctx.action then
                return actions.indeterminate
            end
            if not ( ctx.action == "POST" and ctx.entity.path == "/motd" ) then
                return actions.notapplicable
            end
            -- target end
            
            -- r1 rule begin
            local function r1(ctx, actions, handlers)
                if not ctx.subject.role then
                    return actions.indeterminate
                end
                if ( ctx.subject.role == "admin" ) then
                    return actions.permit
                end
                return actions.notapplicable
            end
            -- r1 rule end
            
            
            -- denyunlesspermit rule-combining algorithm begin
            local rules = { r0 = r1 }
            for _, rule in pairs(rules) do
                local decision = rule(ctx, actions, handlers)
                if decision == actions.permit then
                    return actions.permit
                end
            end
            return actions.deny
            -- denyunlesspermit rule-combining algorithm end
            
        end
        -- postMotd policy end
        
        -- getAdmin policy begin
        local function getAdmin(ctx, actions, handlers)
            -- target begin
            if not ctx.entity.path or not ctx.action then
                return actions.indeterminate
            end
            if not ( ctx.action == "GET" and ctx.entity.path == "/admin" ) then
                return actions.notapplicable
            end
            -- target end
            
            -- r1 rule begin
            local function r1(ctx, actions, handlers)
                if not ctx.subject.role then
                    return actions.indeterminate
                end
                if ( ctx.subject.role == "admin" ) then
                    return actions.permit
                end
                return actions.notapplicable
            end
            -- r1 rule end
            
            
            -- denyunlesspermit rule-combining algorithm begin
            local rules = { r0 = r1 }
            for _, rule in pairs(rules) do
                local decision = rule(ctx, actions, handlers)
                if decision == actions.permit then
                    return actions.permit
                end
            end
            return actions.deny
            -- denyunlesspermit rule-combining algorithm end
            
        end
        -- getAdmin policy end
        
        -- getStats policy begin
        local function getStats(ctx, actions, handlers)
            -- target begin
            if not ctx.entity.path or not ctx.action then
                return actions.indeterminate
            end
            if not ( ctx.action == "GET" and ctx.entity.path == "/stats" ) then
                return actions.notapplicable
            end
            -- target end
            
            -- r1 rule begin
            local function r1(ctx, actions, handlers)
                if not ctx.subject.ip then
                    return actions.indeterminate
                end
                if ( ctx.subject.ip == "127.0.0.1" ) then
                    return actions.permit
                end
                return actions.notapplicable
            end
            -- r1 rule end
            
            
            -- denyunlesspermit rule-combining algorithm begin
            local rules = { r0 = r1 }
            for _, rule in pairs(rules) do
                local decision = rule(ctx, actions, handlers)
                if decision == actions.permit then
                    return actions.permit
                end
            end
            return actions.deny
            -- denyunlesspermit rule-combining algorithm end
            
        end
        -- getStats policy end
        
        
        -- denyunlesspermit policy-combining algorithm begin
        local policies = { p0 = getIndex, p1 = getMotd, p2 = postMotd, p3 = getAdmin, p4 = getStats }
        for _, policy in pairs(policies) do
            local decision = policy(ctx, actions, handlers)
            if decision == actions.permit then
                return actions.permit
            end
        end
        return actions.deny
        -- denyunlesspermit policy-combining algorithm end
        
    end
    -- example.mainPolicy policy set end
    
end
__example(example)
-- example namespace end

function __main(ctx, actions, handlers)
    return example.mainPolicy(ctx, actions, handlers)
end
