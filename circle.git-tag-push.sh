#!/usr/bin/env ruby

if ARGV.length >= 1 && ARGV[0]
  tag = "release-#{ARGV[0]}"
  exec "git tag #{tag} ; git push origin #{tag}"
end
