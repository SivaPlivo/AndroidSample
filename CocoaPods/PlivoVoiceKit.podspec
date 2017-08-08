Pod::Spec.new do |s|
s.name             = 'PlivoVoiceKit'
s.version          = '1.0.0'
s.summary          = 'Plivo Voice SDK for iOS'
s.description      = 'The Plivo iOS SDK allows you to create applications capable of making and receiving calls in your iOS app.'
s.homepage         = 'https://www.plivo.com/'
s.license          = { :type => 'Commercial', :text => "Copyright 2011-2017 Plivo. All rights reserved. Use of this software is subject to the terms and conditions of the Plivo Terms of Service located at https://www.plivo.com/privacy/" }
s.author           = { 'Plivo' => 'support@plivo.com' }
s.source           = { :git => 'https://github.com/SivaPlivo/AndroidSample.git', :tag => s.version.to_s }
s.ios.deployment_target = '8.0'
s.ios.vendored_frameworks = 'PlivoVoiceKit.framework'
s.requires_arc          = true
end
